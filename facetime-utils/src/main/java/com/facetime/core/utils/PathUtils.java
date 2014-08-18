package com.facetime.core.utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * General filename and filepath manipulation utilities.
 * <p>
 * When dealing with filenames you can hit problems when moving from a Windows
 * based development machine to a Unix based production machine.
 * This class aims to help avoid those problems.
 * <p>
 * <b>NOTE</b>: You may be able to avoid using this class entirely simply by
 * using JDK {@link java.io.File File} objects and the two argument constructor
 * {@link java.io.File#File(java.io.File, String) File(File,String)}.
 * <p>
 * Most methods on this class are designed to work the same on both Unix and Windows.
 * Those that don't include 'System', 'Unix' or 'Windows' in their getName.
 * <p>
 * Most methods recognise both separators (forward and back), and both
 * sets of prefixes. See the javadoc of each method for details.
 * <p>
 * This class defines six components within a filename
 * (example C:\dev\project\file.txt):
 * <ul>
 * <li>the prefix - C:\</li>
 * <li>the path - dev\project\</li>
 * <li>the full path - C:\dev\project\</li>
 * <li>the getName - file.txt</li>
 * <li>the base getName - file</li>
 * <li>the extension - txt</li>
 * </ul>
 * Note that this class works best if directory filenames end with a separator.
 * If you omit the last separator, it is impossible to determine if the filename
 * corresponds to a file or a directory. As a result, we have chosen to say
 * it corresponds to a file.
 * <p>
 * This class only supports Unix and Windows style names.
 * Prefixes are matched as follows:
 * <pre>
 * Windows:
 * a\b\c.txt           --> ""          --> relative
 * \a\b\c.txt          --> "\"         --> current drive absolute
 * C:a\b\c.txt         --> "C:"        --> drive relative
 * C:\a\b\c.txt        --> "C:\"       --> absolute
 * \\server\a\b\c.txt  --> "\\server\" --> UNC
 *
 * Unix:
 * a/b/c.txt           --> ""          --> relative
 * /a/b/c.txt          --> "/"         --> absolute
 * ~/a/b/c.txt         --> "~/"        --> current user
 * ~                   --> "~/"        --> current user (slash added)
 * ~user/a/b/c.txt     --> "~user/"    --> named user
 * ~user               --> "~user/"    --> named user (slash added)
 * </pre>
 * Both prefix styles are matched always, irrespective of the machine that you are
 * currently running on.
 */
public class PathUtils {

	/**
	 * The extension separator character.
	 */
	private static final char EXTENSION_SEPARATOR = '.';

	/**
	 * The Unix separator character.
	 */
	private static final char UNIX_SEPARATOR = '/';

	/**
	 * The Windows separator character.
	 */
	private static final char WINDOWS_SEPARATOR = '\\';

	/**
	 * The system separator character.
	 */
	private static final char SYSTEM_SEPARATOR = File.separatorChar;

	/**
	 * The separator character that is the opposite of the system separator.
	 */
	private static final char OTHER_SEPARATOR;

	static {

		//			LINE_SEPARATOR = AccessController.doPrivileged(new PrivilegedAction<String>()
		//			{
		//				public String run()
		//				{
		//					return System.getProperty("line.separator");
		//				}
		//			});
		//		
		if (SYSTEM_SEPARATOR == WINDOWS_SEPARATOR)
			OTHER_SEPARATOR = UNIX_SEPARATOR;
		else
			OTHER_SEPARATOR = WINDOWS_SEPARATOR;
	}

	public static final String PARENT_PATH = "..";

	public static final String CURRENT_PATH = ".";

	/**
	 * Normalize the path by suppressing sequences like "path/.." and
	 * inner simple dots.
	 * <p>The result is convenient for path comparison. For other uses,
	 * notice that Windows separators ("\") are replaced by simple slashes.
	 * @param path the original path
	 * @return the normalized path
	 */
	public static String cleanPath(String path) {

		String pathToUse = StringUtils.replaceChars(path, WINDOWS_SEPARATOR, UNIX_SEPARATOR);
		// Strip prefix from path to analyze, to not treat it as part of the
		// first path element. This is necessary to correctly parse paths like
		// "file:core/../core/io/Resource.class", where the ".." should just
		// strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(":");
		String prefix = "";
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			pathToUse = pathToUse.substring(prefixIndex + 1);
		}

		String[] pathArray = StringUtils.split(pathToUse, UNIX_SEPARATOR);
		List<String> pathElements = new LinkedList<String>();
		int tops = 0;

		for (int i = pathArray.length - 1; i >= 0; i--)
			if (CURRENT_PATH.equals(pathArray[i])) {
				// Points to current directory - drop it.
			} else if (PARENT_PATH.equals(pathArray[i]))
				// Registering top path found.
				tops++;
			else if (tops > 0)
				// Merging path element with corresponding to top path.
				tops--;
			else
				// Normal path element found.
				pathElements.add(0, pathArray[i]);
		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++)
			pathElements.add(0, PARENT_PATH);

		return prefix + StringUtils.toDelimitedString(pathElements, String.valueOf(UNIX_SEPARATOR));
	}

	//-----------------------------------------------------------------------
	/**
	 * Concatenates a filename to a base path using normal command line style rules.
	 * <p>
	 * The effect is equivalent to resultant directory after changing
	 * directory to the first argument, followed by changing directory to
	 * the second argument.
	 * <p>
	 * The first argument is the base path, the second is the path to concatenate.
	 * The returned path is always normalized via {@link #normalize(String)},
	 * thus <code>..</code> is handled.
	 * <p>
	 * If <code>pathToAdd</code> is absolute (has an absolute prefix), then
	 * it will be normalized and returned.
	 * Otherwise, the paths will be joined, normalized and returned.
	 * <p>
	 * The output will be the same on both Unix and Windows except
	 * for the separator character.
	 * <pre>
	 * /foo/ + bar          -->   /foo/bar
	 * /foo + bar           -->   /foo/bar
	 * /foo + /bar          -->   /bar
	 * /foo + C:/bar        -->   C:/bar
	 * /foo + C:bar         -->   C:bar (*)
	 * /foo/a/ + ../bar     -->   foo/bar
	 * /foo/ + ../../bar    -->   null
	 * /foo/ + /bar         -->   /bar
	 * /foo/.. + /bar       -->   /bar
	 * /foo + bar/c.txt     -->   /foo/bar/c.txt
	 * /foo/c.txt + bar     -->   /foo/c.txt/bar (!)
	 * </pre>
	 * (*) Note that the Windows relative drive prefix is unreliable when
	 * used with this method.
	 * (!) Note that the first parameter must be a path. If it ends with a getName, then
	 * the getName will be built into the concatenated path. If this might be a problem,
	 * use {@link #getFullPath(String)} on the base path argument.
	 *
	 * @param basePath  the base path to attach to, always treated as a path
	 * @param fullFilenameToAdd  the filename (or path) to attach to the base
	 * @return the concatenated path, or null if invalid
	 */
	public static String concat(String basePath, String fullFilenameToAdd) {
		int prefix = getPrefixLength(fullFilenameToAdd);
		if (prefix < 0)
			return null;
		if (prefix > 0)
			return normalize(fullFilenameToAdd);
		if (basePath == null)
			return null;
		int len = basePath.length();
		if (len == 0)
			return normalize(fullFilenameToAdd);
		char ch = basePath.charAt(len - 1);
		if (isSeparator(ch))
			return normalize(basePath + fullFilenameToAdd);
		else
			return normalize(basePath + '/' + fullFilenameToAdd);
	}

	/**
	 * Checks whether two filenames are equal exactly.
	 */
	public static boolean equals(String filename1, String filename2) {
		return equals(filename1, filename2, false);
	}

	/**
	 * Checks whether two filenames are equal using the case rules of the system.
	 */
	public static boolean equalsOnSystem(String filename1, String filename2) {
		return equals(filename1, filename2, true);
	}

	/**
	 * Gets the base getName, minus the full path and extension, from a full filename.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The text after the last forward or backslash and before the last dot is returned.
	 * <pre>
	 * a/b/c.txt --> c
	 * a.txt     --> a
	 * a/b/c     --> c
	 * a/b/c/    --> ""
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the getName of the file without the path, or an empty string if none exists
	 */
	public static String getBaseName(String filename) {
		return removeExtension(getName(filename));
	}

	/**
	 * Gets the extension of a filename.
	 * <p>
	 * This method returns the textual part of the filename after the last dot.
	 * There must be no directory separator after the dot.
	 * <pre>
	 * foo.txt      --> "txt"
	 * a/b/c.jpg    --> "jpg"
	 * a/b.txt/c    --> ""
	 * a/b/c        --> ""
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename the filename to retrieve the extension of.
	 * @return the extension of the file or an empty string if none exists.
	 */
	public static String getExtension(String filename) {
		if (filename == null)
			return null;
		int index = indexOfExtension(filename);
		if (index == -1)
			return StringPool.EMPTY;
		else
			return filename.substring(index + 1);
	}

	/**
	 * Gets the full path from a full filename, which is the prefix + path.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The method is entirely text based, and returns the text before and
	 * including the last forward or backslash.
	 * <pre>
	 * C:\a\b\c.txt --> C:\a\b\
	 * ~/a/b/c.txt  --> ~/a/b/
	 * a.txt        --> ""
	 * a/b/c        --> a/b/
	 * a/b/c/       --> a/b/c/
	 * C:           --> C:
	 * C:\          --> C:\
	 * ~            --> ~/
	 * ~/           --> ~/
	 * ~user        --> ~user/
	 * ~user/       --> ~user/
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the path of the file, an empty string if none exists, null if invalid
	 */
	public static String getFullPath(String filename) {
		return doGetFullPath(filename, true);
	}

	// ---------------------------------------------------------------- separator conversion

	/**
	 * Gets the full path from a full filename, which is the prefix + path,
	 * and also excluding the final directory separator.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The method is entirely text based, and returns the text before the
	 * last forward or backslash.
	 * <pre>
	 * C:\a\b\c.txt --> C:\a\b
	 * ~/a/b/c.txt  --> ~/a/b
	 * a.txt        --> ""
	 * a/b/c        --> a/b
	 * a/b/c/       --> a/b/c
	 * C:           --> C:
	 * C:\          --> C:\
	 * ~            --> ~
	 * ~/           --> ~
	 * ~user        --> ~user
	 * ~user/       --> ~user
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the path of the file, an empty string if none exists, null if invalid
	 */
	public static String getFullPathNoEndSeparator(String filename) {
		return doGetFullPath(filename, false);
	}

	/**
	 * Gets the getName minus the path from a full filename.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The text after the last forward or backslash is returned.
	 * <pre>
	 * a/b/c.txt --> c.txt
	 * a.txt     --> a.txt
	 * a/b/c     --> c
	 * a/b/c/    --> ""
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the getName of the file without the path, or an empty string if none exists
	 */
	public static String getName(String filename) {
		if (filename == null)
			return null;
		int index = indexOfLastSeparator(filename);
		return filename.substring(index + 1);
	}

	/**
	 * Gets the path from a full filename, which excludes the prefix.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The method is entirely text based, and returns the text before and
	 * including the last forward or backslash.
	 * <pre>
	 * C:\a\b\c.txt --> a\b\
	 * ~/a/b/c.txt  --> a/b/
	 * a.txt        --> ""
	 * a/b/c        --> a/b/
	 * a/b/c/       --> a/b/c/
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * <p>
	 * This method drops the prefix from the result.
	 * See {@link #getFullPath(String)} for the method that retains the prefix.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the path of the file, an empty string if none exists, null if invalid
	 */
	public static String getPath(String filename) {
		return doGetPath(filename, 1);
	}

	/**
	 * Gets the path from a full filename, which excludes the prefix, and
	 * also excluding the final directory separator.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The method is entirely text based, and returns the text before the
	 * last forward or backslash.
	 * <pre>
	 * C:\a\b\c.txt --> a\b
	 * ~/a/b/c.txt  --> a/b
	 * a.txt        --> ""
	 * a/b/c        --> a/b
	 * a/b/c/       --> a/b/c
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * <p>
	 * This method drops the prefix from the result.
	 * See {@link #getFullPathNoEndSeparator(String)} for the method that retains the prefix.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the path of the file, an empty string if none exists, null if invalid
	 */
	public static String getPathNoEndSeparator(String filename) {
		return doGetPath(filename, 0);
	}

	/**
	 * Gets the prefix from a full filename, such as <code>C:/</code>
	 * or <code>~/</code>.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The prefix includes the first slash in the full filename where applicable.
	 * <pre>
	 * Windows:
	 * a\b\c.txt           --> ""          --> relative
	 * \a\b\c.txt          --> "\"         --> current drive absolute
	 * C:a\b\c.txt         --> "C:"        --> drive relative
	 * C:\a\b\c.txt        --> "C:\"       --> absolute
	 * \\server\a\b\c.txt  --> "\\server\" --> UNC
	 *
	 * Unix:
	 * a/b/c.txt           --> ""          --> relative
	 * /a/b/c.txt          --> "/"         --> absolute
	 * ~/a/b/c.txt         --> "~/"        --> current user
	 * ~                   --> "~/"        --> current user (slash added)
	 * ~user/a/b/c.txt     --> "~user/"    --> named user
	 * ~user               --> "~user/"    --> named user (slash added)
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * ie. both Unix and Windows prefixes are matched regardless.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the prefix of the file, null if invalid
	 */
	public static String getPrefix(String filename) {
		if (filename == null)
			return null;
		int len = getPrefixLength(filename);
		if (len < 0)
			return null;
		if (len > filename.length())
			return filename + UNIX_SEPARATOR; // we know this only happens for unix
		return filename.substring(0, len);
	}

	// ---------------------------------------------------------------- prefix
	/**
	 * Returns the length of the filename prefix, such as <code>C:/</code> or <code>~/</code>.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * <p>
	 * The prefix length includes the first slash in the full filename
	 * if applicable. Thus, it is possible that the length returned is greater
	 * than the length of the input string.
	 * <pre>
	 * Windows:
	 * a\b\c.txt           --> ""          --> relative
	 * \a\b\c.txt          --> "\"         --> current drive absolute
	 * C:a\b\c.txt         --> "C:"        --> drive relative
	 * C:\a\b\c.txt        --> "C:\"       --> absolute
	 * \\server\a\b\c.txt  --> "\\server\" --> UNC
	 *
	 * Unix:
	 * a/b/c.txt           --> ""          --> relative
	 * /a/b/c.txt          --> "/"         --> absolute
	 * ~/a/b/c.txt         --> "~/"        --> current user
	 * ~                   --> "~/"        --> current user (slash added)
	 * ~user/a/b/c.txt     --> "~user/"    --> named user
	 * ~user               --> "~user/"    --> named user (slash added)
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * ie. both Unix and Windows prefixes are matched regardless.
	 *
	 * @param filename  the filename to find the prefix in, null returns -1
	 * @return the length of the prefix, -1 if invalid or null
	 */
	public static int getPrefixLength(String filename) {
		if (filename == null)
			return -1;
		int len = filename.length();
		if (len == 0)
			return 0;
		char ch0 = filename.charAt(0);
		if (ch0 == ':')
			return -1;
		if (len == 1) {
			if (ch0 == '~')
				return 2; // return a length greater than the input
			return isSeparator(ch0) ? 1 : 0;
		} else {
			if (ch0 == '~') {
				int posUnix = filename.indexOf(UNIX_SEPARATOR, 1);
				int posWin = filename.indexOf(WINDOWS_SEPARATOR, 1);
				if (posUnix == -1 && posWin == -1)
					return len + 1; // return a length greater than the input
				posUnix = posUnix == -1 ? posWin : posUnix;
				posWin = posWin == -1 ? posUnix : posWin;
				return Math.min(posUnix, posWin) + 1;
			}
			char ch1 = filename.charAt(1);
			if (ch1 == ':') {
				ch0 = Character.toUpperCase(ch0);
				if (ch0 >= 'A' && ch0 <= 'Z') {
					if (len == 2 || isSeparator(filename.charAt(2)) == false)
						return 2;
					return 3;
				}
				return -1;

			} else if (isSeparator(ch0) && isSeparator(ch1)) {
				int posUnix = filename.indexOf(UNIX_SEPARATOR, 2);
				int posWin = filename.indexOf(WINDOWS_SEPARATOR, 2);
				if (posUnix == -1 && posWin == -1 || posUnix == 2 || posWin == 2)
					return -1;
				posUnix = posUnix == -1 ? posWin : posUnix;
				posWin = posWin == -1 ? posUnix : posWin;
				return Math.min(posUnix, posWin) + 1;
			} else
				return isSeparator(ch0) ? 1 : 0;
		}
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns the index of the last extension separator character, which is a dot.
	 * <p>
	 * This method also checks that there is no directory separator after the last dot.
	 * To do this it uses {@link #indexOfLastSeparator(String)} which will
	 * handle a file in either Unix or Windows format.
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to find the last path separator in, null returns -1
	 * @return the index of the last separator character, or -1 if there
	 * is no such character
	 */
	public static int indexOfExtension(String filename) {
		if (filename == null)
			return -1;
		int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
		int lastSeparator = indexOfLastSeparator(filename);
		return lastSeparator > extensionPos ? -1 : extensionPos;
	}

	/**
	 * Returns the index of the last directory separator character.
	 * <p>
	 * This method will handle a file in either Unix or Windows format.
	 * The position of the last forward or backslash is returned.
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to find the last path separator in, null returns -1
	 * @return the index of the last separator character, or -1 if there is no such character
	 */
	public static int indexOfLastSeparator(String filename) {
		if (filename == null)
			return -1;
		int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
		int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
		return Math.max(lastUnixPos, lastWindowsPos);
	}

	// ---------------------------------------------------------------- normalization
	/**
	 * Normalizes a path, removing double and single dot path steps.
	 * <p>
	 * This method normalizes a path to a standard format.
	 * The input may contain separators in either Unix or Windows format.
	 * The output will contain separators in the format of the system.
	 * <p>
	 * A trailing slash will be retained.
	 * A double slash will be merged to a single slash (but UNC names are handled).
	 * A single dot path segment will be removed.
	 * A double dot will cause that path segment and the one before to be removed.
	 * If the double dot has no parent path segment to work with, <code>null</code>
	 * is returned.
	 * <p>
	 * The output will be the same on both Unix and Windows except
	 * for the separator character.
	 * <pre>
	 * /foo//               -->   /foo/
	 * /foo/./              -->   /foo/
	 * /foo/../bar          -->   /bar
	 * /foo/../bar/         -->   /bar/
	 * /foo/../bar/../baz   -->   /baz
	 * //foo//./bar         -->   /foo/bar
	 * /../                 -->   null
	 * ../foo               -->   null
	 * foo/bar/..           -->   foo/
	 * foo/../../bar        -->   null
	 * foo/../bar           -->   bar
	 * //server/foo/../bar  -->   //server/bar
	 * //server/../bar      -->   null
	 * C:\foo\..\bar        -->   C:\bar
	 * C:\..\bar            -->   null
	 * ~/foo/../bar/        -->   ~/bar/
	 * ~/../bar             -->   null
	 * </pre>
	 * (Note the file separator returned will be correct for Windows/Unix)
	 *
	 * @param filename  the filename to normalize, null returns null
	 * @return the normalized filename, or null if invalid
	 */
	public static String normalize(String filename) {
		return normalize(filename, true);
	}

	/**
	 * Normalizes a path, removing double and single dot path steps,
	 * and removing any final directory separator.
	 * <p>
	 * This method normalizes a path to a standard format.
	 * The input may contain separators in either Unix or Windows format.
	 * The output will contain separators in the format of the system.
	 * <p>
	 * A trailing slash will be removed.
	 * A double slash will be merged to a single slash (but UNC names are handled).
	 * A single dot path segment will be removed.
	 * A double dot will cause that path segment and the one before to be removed.
	 * If the double dot has no parent path segment to work with, <code>null</code>
	 * is returned.
	 * <p>
	 * The output will be the same on both Unix and Windows except
	 * for the separator character.
	 * <pre>
	 * /foo//               -->   /foo
	 * /foo/./              -->   /foo
	 * /foo/../bar          -->   /bar
	 * /foo/../bar/         -->   /bar
	 * /foo/../bar/../baz   -->   /baz
	 * //foo//./bar         -->   /foo/bar
	 * /../                 -->   null
	 * ../foo               -->   null
	 * foo/bar/..           -->   foo
	 * foo/../../bar        -->   null
	 * foo/../bar           -->   bar
	 * //server/foo/../bar  -->   //server/bar
	 * //server/../bar      -->   null
	 * C:\foo\..\bar        -->   C:\bar
	 * C:\..\bar            -->   null
	 * ~/foo/../bar/        -->   ~/bar
	 * ~/../bar             -->   null
	 * </pre>
	 * (Note the file separator returned will be correct for Windows/Unix)
	 *
	 *
	 * @param filename  the filename
	 * @param keepSeparator  true to keep the final separator
	 * @return the normalized filename
	 */
	public static String normalize(String filename, boolean keepSeparator) {
		if (filename == null || filename.length() == 0)
			return filename;
		int prefix = getPrefixLength(filename);
		if (prefix < 0)
			return null;
		int size = filename.length();
		char[] array = new char[size + 2]; // +1 for possible extra slash, +2 for arraycopy
		filename.getChars(0, filename.length(), array, 0);

		// fix separators throughout
		for (int i = 0; i < array.length; i++)
			if (array[i] == OTHER_SEPARATOR)
				array[i] = SYSTEM_SEPARATOR;

		// fill extra separator on the end to simplify code below
		boolean lastIsDirectory = true;
		if (array[size - 1] != SYSTEM_SEPARATOR) {
			array[size] = SYSTEM_SEPARATOR;
			size++;
			lastIsDirectory = false;
		}

		// adjoining slashes
		for (int i = prefix + 1; i < size; i++)
			if (array[i] == SYSTEM_SEPARATOR && array[i - 1] == SYSTEM_SEPARATOR) {
				System.arraycopy(array, i, array, i - 1, size - i);
				size--;
				i--;
			}

		// dot slash
		for (int i = prefix + 1; i < size; i++)
			if (array[i] == SYSTEM_SEPARATOR && array[i - 1] == '.'
					&& (i == prefix + 1 || array[i - 2] == SYSTEM_SEPARATOR)) {
				if (i == size - 1)
					lastIsDirectory = true;
				System.arraycopy(array, i + 1, array, i - 1, size - i);
				size -= 2;
				i--;
			}

		// double dot slash
		outer: for (int i = prefix + 2; i < size; i++)
			if (array[i] == SYSTEM_SEPARATOR && array[i - 1] == '.' && array[i - 2] == '.'
					&& (i == prefix + 2 || array[i - 3] == SYSTEM_SEPARATOR)) {
				if (i == prefix + 2)
					return null;
				if (i == size - 1)
					lastIsDirectory = true;
				int j;
				for (j = i - 4; j >= prefix; j--)
					if (array[j] == SYSTEM_SEPARATOR) {
						// remove b/../ from a/b/../c
						System.arraycopy(array, i + 1, array, j + 1, size - i);
						size -= i - j;
						i = j + 1;
						continue outer;
					}
				// remove a/../ from a/../c
				System.arraycopy(array, i + 1, array, prefix, size - i);
				size -= i + 1 - prefix;
				i = prefix + 1;
			}

		if (size <= 0)
			return "";
		if (size <= prefix)
			return new String(array, 0, size);
		if (lastIsDirectory && keepSeparator)
			return new String(array, 0, size); // keep trailing separator
		return new String(array, 0, size - 1); // lose trailing separator
	}

	/**
	 * Removes the extension from a filename.
	 * <p>
	 * This method returns the textual part of the filename before the last dot.
	 * There must be no directory separator after the dot.
	 * <pre>
	 * foo.txt    --> foo
	 * a\b\c.jpg  --> a\b\c
	 * a\b\c      --> a\b\c
	 * a.b\c      --> a.b\c
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename  the filename to query, null returns null
	 * @return the filename minus the extension
	 */
	public static String removeExtension(String filename) {
		if (filename == null)
			return null;
		int index = indexOfExtension(filename);
		if (index == -1)
			return filename;
		else
			return filename.substring(0, index);
	}

	/**
	 * Converts all separators to the system separator.
	 *
	 * @param path  the path to be changed, null ignored
	 * @return the updated path
	 */
	public static String separatorsToSystem(String path) {
		if (path == null)
			return null;
		if (SYSTEM_SEPARATOR == WINDOWS_SEPARATOR)
			return separatorsToWindows(path);
		else
			return separatorsToUnix(path);
	}

	/**
	 * Converts all separators to the Unix separator of forward slash.
	 *
	 * @param path  the path to be changed, null ignored
	 * @return the updated path
	 */
	public static String separatorsToUnix(String path) {
		if (path == null || path.indexOf(WINDOWS_SEPARATOR) == -1)
			return path;
		return path.replace(WINDOWS_SEPARATOR, UNIX_SEPARATOR);
	}

	/**
	 * Converts all separators to the Windows separator of backslash.
	 *
	 * @param path  the path to be changed, null ignored
	 * @return the updated path
	 */
	public static String separatorsToWindows(String path) {
		if (path == null || path.indexOf(UNIX_SEPARATOR) == -1)
			return path;
		return path.replace(UNIX_SEPARATOR, WINDOWS_SEPARATOR);
	}

	/**
	 * Splits filename into a array of four Strings containing prefix, path, basename and extension.
	 * Path will contain ending separator.
	 */
	public static String[] split(String filename) {
		String prefix = getPrefix(filename);
		if (prefix == null)
			prefix = StringPool.EMPTY;
		int lastSeparatorIndex = indexOfLastSeparator(filename);
		int lastExtensionIndex = indexOfExtension(filename);

		String path;
		String baseName;
		String extension;

		if (lastSeparatorIndex == -1) {
			path = StringPool.EMPTY;
			if (lastExtensionIndex == -1) {
				baseName = filename.substring(prefix.length());
				extension = StringPool.EMPTY;
			} else {
				baseName = filename.substring(prefix.length(), lastExtensionIndex);
				extension = filename.substring(lastExtensionIndex + 1);
			}
		} else {
			path = filename.substring(prefix.length(), lastSeparatorIndex + 1);
			if (lastExtensionIndex == -1) {
				baseName = filename.substring(prefix.length() + path.length());
				extension = StringPool.EMPTY;
			} else {
				baseName = filename.substring(prefix.length() + path.length(), lastExtensionIndex);
				extension = filename.substring(lastExtensionIndex + 1);
			}
		}
		return new String[] { prefix, path, baseName, extension };
	}

	/**
	 * 根据root设置path为相对于root的相对路径
	 *
	 * @param root
	 * @param path
	 * @return
	 */
	public static String toRelativePath(String root, String path) {
		assert path != null;
		root = toUnixPath(root);
		path = toUnixPath(path);
		if (path.indexOf(':') < 0 && !(path.charAt(0) == UNIX_SEPARATOR))
			path = UNIX_SEPARATOR + path;
		path = StringUtils.replaceOnce(path, root, "");
		return path;
	}

	/**
	 * 强转一个路径为unix风格，使用"/"路径分隔符
	 * @param path
	 * @return
	 */
	public static String toUnixPath(String path) {
		String p = path.replaceAll("\\+", "\\");
		p = StringUtils.replaceChars(p, WINDOWS_SEPARATOR, UNIX_SEPARATOR);
		p = p.replaceAll("/+", "/");
		//p = normalize(p);
		return p;
	}

	/**
	 * Does the work of getting the path.
	 *
	 * @param filename  the filename
	 * @param includeSeparator  true to include the end separator
	 * @return the path
	 */
	private static String doGetFullPath(String filename, boolean includeSeparator) {
		if (filename == null)
			return null;
		int prefix = getPrefixLength(filename);
		if (prefix < 0)
			return null;
		if (prefix >= filename.length())
			if (includeSeparator)
				return getPrefix(filename); // fill end slash if necessary
			else
				return filename;
		int index = indexOfLastSeparator(filename);
		if (index < 0)
			return filename.substring(0, prefix);
		int end = index + (includeSeparator ? 1 : 0);
		return filename.substring(0, end);
	}

	/**
	 * Does the work of getting the path.
	 *
	 * @param filename  the filename
	 * @param separatorAdd  0 to omit the end separator, 1 to return it
	 * @return the path
	 */
	private static String doGetPath(String filename, int separatorAdd) {
		if (filename == null)
			return null;
		int prefix = getPrefixLength(filename);
		if (prefix < 0)
			return null;
		int index = indexOfLastSeparator(filename);
		if (prefix >= filename.length() || index < 0)
			return StringPool.EMPTY;
		return filename.substring(prefix, index + separatorAdd);
	}

	/**
	 * Checks whether two filenames are equal optionally using the case rules of the system.
	 * <p>
	 *
	 * @param filename1  the first filename to query, may be null
	 * @param filename2  the second filename to query, may be null
	 * @param system  whether to use the system (windows or unix)
	 * @return true if the filenames are equal, null equals null
	 */
	private static boolean equals(String filename1, String filename2, boolean system) {
		//noinspection StringEquality
		if (filename1 == filename2)
			return true;
		if (filename1 == null || filename2 == null)
			return false;
		if (system && SYSTEM_SEPARATOR == WINDOWS_SEPARATOR)
			return filename1.equalsIgnoreCase(filename2);
		else
			return filename1.equals(filename2);
	}

	/**
	 * Checks if the character is a separator.
	 */
	private static boolean isSeparator(char ch) {
		return ch == UNIX_SEPARATOR || ch == WINDOWS_SEPARATOR;
	}
}