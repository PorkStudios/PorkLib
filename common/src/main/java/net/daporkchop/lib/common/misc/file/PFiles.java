/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.common.misc.file;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.exception.file.CannotCreateDirectoryException;
import net.daporkchop.lib.common.util.exception.file.CannotCreateFileException;
import net.daporkchop.lib.common.util.exception.file.CannotDeleteFileException;
import net.daporkchop.lib.common.util.exception.file.NoSuchDirectoryException;
import net.daporkchop.lib.common.util.exception.file.NoSuchFileException;
import net.daporkchop.lib.common.util.exception.file.NotADirectoryException;
import net.daporkchop.lib.common.util.exception.file.NotAFileException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

/**
 * Helper class for working with {@link File}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PFiles {
    /**
     * Ensures that a directory exists and is a directory, creating a new directory if it doesn't exist and throwing an exception in case
     * of failure.
     *
     * @param directory the directory to ensure the existence of
     * @return the directory
     * @throws CannotCreateDirectoryException if the directory could not be created
     * @throws NotADirectoryException         if the given path exists, but is not a directory
     */
    public File ensureDirectoryExists(@NonNull File directory) throws CannotCreateDirectoryException, NotADirectoryException {
        if (!directory.exists() && !directory.mkdirs() && !directory.exists()) { //second check in case the directory was created by another thread
            throw new CannotCreateDirectoryException(directory);
        } else if (!directory.isDirectory()) {
            throw new NotADirectoryException(directory);
        } else {
            return directory;
        }
    }

    /**
     * Ensures that the parent directory of the given file exists and is a directory, creating a new directory if it
     * doesn't exist and throwing an exception in case of failure.
     *
     * @param file the file of which to ensure the existence of the parent directory
     * @return the file
     * @throws CannotCreateDirectoryException if the parent directory could not be created
     * @throws NotADirectoryException         if the given file's parent is not a directory
     */
    public static File ensureParentDirectoryExists(@NonNull File file) throws CannotCreateDirectoryException, NotADirectoryException    {
        ensureDirectoryExists(file.getAbsoluteFile().getParentFile());
        return file;
    }

    /**
     * Ensures that a file exists and is a file, creating a new file and all required parent directories if it doesn't exist and throwing
     * an exception in case of failure.
     *
     * @param file the file to ensure the existence of
     * @return the file
     * @throws CannotCreateFileException      if the file could not be created
     * @throws CannotCreateDirectoryException if the file's parent directory could not be created
     * @throws NotAFileException              if the given path exists, but is not a file
     * @throws NotADirectoryException         if the given path's parent file exists, but is not a directory
     */
    public File ensureFileExists(@NonNull File file) throws CannotCreateFileException, CannotCreateDirectoryException, NotAFileException, NotADirectoryException {
        if (!file.exists()) {
            if (!file.isAbsolute()) {
                //ensure file is absolute file so that we can get the parent file if needed
                file = file.getAbsoluteFile();
            }

            File parentFile = file.getParentFile();
            if (parentFile != null) {
                //only attempt to create parent file if there is one
                ensureDirectoryExists(file.getParentFile());
            }

            try {
                if (!file.createNewFile() && !file.exists()) { //second check in case the file was created by another thread
                    throw new CannotCreateFileException(file);
                }
            } catch (IOException e) {
                throw new CannotCreateFileException(file, e);
            }
        }
        if (!file.isFile()) {
            throw new NotAFileException(file);
        }
        return file;
    }

    /**
     * Checks if the given directory exists.
     *
     * @param directory the directory to check the existence of
     * @return the directory
     * @throws NotADirectoryException if the given path exists, but is not a directory
     */
    public boolean checkDirectoryExists(@NonNull File directory) throws NotADirectoryException {
        if (directory.exists()) {
            if (directory.isDirectory()) {
                return true;
            } else {
                throw new NotADirectoryException(directory);
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if the given file exists.
     *
     * @param file the file to check the existence of
     * @return whether or not the file exists
     * @throws NotAFileException if the given path exists, but is not a file
     */
    public boolean checkFileExists(@NonNull File file) throws NotAFileException {
        if (file.exists()) {
            if (file.isFile()) {
                return true;
            } else {
                throw new NotAFileException(file);
            }
        } else {
            return false;
        }
    }

    /**
     * Asserts that the given directory exists.
     *
     * @param directory the directory to assert the existence of
     * @return the directory
     * @throws NoSuchDirectoryException if the directory does not exist
     * @throws NotADirectoryException   if the given path exists, but is not a directory
     */
    public File assertDirectoryExists(@NonNull File directory) throws NoSuchDirectoryException, NotADirectoryException {
        if (directory.exists()) {
            if (directory.isDirectory()) {
                return directory;
            } else {
                throw new NotADirectoryException(directory);
            }
        } else {
            throw new NoSuchDirectoryException(directory);
        }
    }

    /**
     * Asserts that the given file exists.
     *
     * @param file the file to assert the existence of
     * @return the file
     * @throws NoSuchFileException if the file does not exist
     * @throws NotAFileException   if the given path exists, but is not a file
     */
    public File assertFileExists(@NonNull File file) throws NoSuchFileException, NotAFileException {
        if (file.exists()) {
            if (file.isFile()) {
                return file;
            } else {
                throw new NotAFileException(file);
            }
        } else {
            throw new NoSuchFileException(file);
        }
    }

    /**
     * Deletes a file or directory.
     * <p>
     * If the given file is a directory, then the directory's contents will first be deleted recursively.
     *
     * @param file the file to be deleted
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    public void rm(@NonNull File file) throws CannotDeleteFileException {
        while (file.exists()) {
            if (file.isDirectory()) {
                File[] files;
                while ((files = file.listFiles()) != null && files.length != 0) {
                    for (File f : files) {
                        rm(f);
                    }
                }
            }
            if (!file.delete()) {
                throw new CannotDeleteFileException(file);
            }
        }
    }

    /**
     * Deletes a file or directory.
     * <p>
     * If the given file is a directory, then the directory's contents will first be deleted recursively and in parallel across multiple threads. This
     * may be faster than {@link #rm(File)} for deleting large directory trees.
     *
     * @param file the file to be deleted
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    public void rmParallel(@NonNull File file) throws CannotDeleteFileException {
        try {
            if (false) {
                //trick the compiler into letting us catch ExecutionException, which is thrown unsafely
                throw new ExecutionException(null);
            }
            while (file.exists()) {
                if (file.isDirectory()) {
                    File[] files;
                    while ((files = file.listFiles()) != null && files.length != 0) {
                        StreamSupport.stream(Arrays.spliterator(files), true).forEach(PFiles::rmParallel);
                    }
                }
                if (!file.delete()) {
                    throw new CannotDeleteFileException(file);
                }
            }
        } catch (ExecutionException e) {
            if (e.getCause() instanceof CannotDeleteFileException) {
                throw (CannotDeleteFileException) e.getCause();
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Recursively deletes all contents of a given directory.
     * <p>
     * If the given directory does not exist, nothing will be done.
     *
     * @param directory the directory whose contents are to be deleted
     * @throws NotADirectoryException    if the given path exists, but is not a directory
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    public void rmContents(@NonNull File directory) throws NotADirectoryException, CannotDeleteFileException {
        if (checkDirectoryExists(directory)) {
            File[] files;
            while ((files = directory.listFiles()) != null && files.length != 0) {
                for (File f : files) {
                    rm(f);
                }
            }
        }
    }

    /**
     * Recursively deletes all contents of a given directory.
     * <p>
     * Contents will be deleted in parallel across multiple threads. This may be faster than {@link #rmContents(File)} for deleting large directory trees.
     * <p>
     * If the given directory does not exist, nothing will be done.
     *
     * @param directory the directory whose contents are to be deleted
     * @throws NotADirectoryException    if the given path exists, but is not a directory
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    public void rmContentsParallel(@NonNull File directory) throws NotADirectoryException, CannotDeleteFileException {
        if (checkDirectoryExists(directory)) {
            try {
                if (false) {
                    //trick the compiler into letting us catch ExecutionException, which is thrown unsafely
                    throw new ExecutionException(null);
                }
                File[] files;
                while ((files = directory.listFiles()) != null && files.length != 0) {
                    StreamSupport.stream(Arrays.spliterator(files), true).forEach(PFiles::rmParallel);
                }
            } catch (ExecutionException e) {
                if (e.getCause() instanceof CannotDeleteFileException) {
                    throw (CannotDeleteFileException) e.getCause();
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
