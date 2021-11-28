/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Stream;
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
     * Ensures that a directory exists and is a directory, creating a new directory if it doesn't exist and throwing an exception in case
     * of failure.
     *
     * @param directory the directory to ensure the existence of
     * @return the directory
     * @throws CannotCreateDirectoryException if the directory could not be created
     * @throws NotADirectoryException         if the given path exists, but is not a directory
     */
    public Path ensureDirectoryExists(@NonNull Path directory) throws CannotCreateDirectoryException, NotADirectoryException {
        try {
            return Files.createDirectories(directory);
        } catch (FileAlreadyExistsException e) {
            throw new NotADirectoryException(directory, e);
        } catch (IOException e) {
            if (Files.exists(directory) && !Files.isDirectory(directory)) { //the implementation doesn't HAVE to throw FileAlreadyExistsException, so we check here just in case
                throw new NotADirectoryException(directory, e);
            } else {
                throw new CannotCreateDirectoryException(directory, e);
            }
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
    public static File ensureParentDirectoryExists(@NonNull File file) throws CannotCreateDirectoryException, NotADirectoryException {
        ensureDirectoryExists(file.getAbsoluteFile().getParentFile());
        return file;
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
    public static Path ensureParentDirectoryExists(@NonNull Path file) throws CannotCreateDirectoryException, NotADirectoryException {
        ensureDirectoryExists(file.getParent());
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
                ensureDirectoryExists(parentFile);
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
    public Path ensureFileExists(@NonNull Path file) throws CannotCreateFileException, CannotCreateDirectoryException, NotAFileException, NotADirectoryException {
        if (!Files.exists(file)) {
            ensureParentDirectoryExists(file);

            try {
                return Files.createFile(file);
            } catch (FileAlreadyExistsException e) {
                //file was created by another thread, swallow exception
            } catch (IOException e) {
                //the implementation doesn't HAVE to throw FileAlreadyExistsException, so we check here just in case
                if (Files.isRegularFile(file)) { //the file was created by another thread and is a regular file, all is well
                    return file;
                } else {
                    throw new CannotCreateFileException(file, e);
                }
            }
        }
        if (!Files.isRegularFile(file)) {
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
     * Checks if the given directory exists.
     *
     * @param directory the directory to check the existence of
     * @return the directory
     * @throws NotADirectoryException if the given path exists, but is not a directory
     */
    public boolean checkDirectoryExists(@NonNull Path directory) throws NotADirectoryException {
        if (Files.isDirectory(directory)) {
            return true;
        } else if (Files.exists(directory)) {
            throw new NotADirectoryException(directory);
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
     * Checks if the given file exists.
     *
     * @param file the file to check the existence of
     * @return whether or not the file exists
     * @throws NotAFileException if the given path exists, but is not a file
     */
    public boolean checkFileExists(@NonNull Path file) throws NotAFileException {
        if (Files.isRegularFile(file)) {
            return true;
        } else if (Files.exists(file)) {
            throw new NotAFileException(file);
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
     * Asserts that the given directory exists.
     *
     * @param directory the directory to assert the existence of
     * @return the directory
     * @throws NoSuchDirectoryException if the directory does not exist
     * @throws NotADirectoryException   if the given path exists, but is not a directory
     */
    public Path assertDirectoryExists(@NonNull Path directory) throws NoSuchDirectoryException, NotADirectoryException {
        if (Files.isDirectory(directory)) {
            return directory;
        } else if (Files.exists(directory)) {
            throw new NotADirectoryException(directory);
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
     * Asserts that the given file exists.
     *
     * @param file the file to assert the existence of
     * @return the file
     * @throws NoSuchFileException if the file does not exist
     * @throws NotAFileException   if the given path exists, but is not a file
     */
    public Path assertFileExists(@NonNull Path file) throws NoSuchFileException, NotAFileException {
        if (Files.isRegularFile(file)) {
            return file;
        } else if (Files.exists(file)) {
            throw new NotAFileException(file);
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
     * Deletes a path.
     * <p>
     * If the given path is a directory, then the directory's contents will first be deleted recursively.
     *
     * @param path the path to be deleted
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    public void rm(@NonNull Path path) throws CannotDeleteFileException {
        while (true) {
            try {
                //we don't care about the return value - it can either be true, indicating that it was deleted, or false, indicating that it didn't exist
                //  in the first place.
                Files.deleteIfExists(path); 
                return;
            } catch (DirectoryNotEmptyException e) {
                //the path is a non-empty directory, swallow exception and fall through to directory-specific handling code
            } catch (IOException e) {
                //the implementation doesn't HAVE to throw DirectoryNotEmptyException, so we check here just in case
                if (Files.isDirectory(path)) {
                    //the path is a non-empty directory, swallow exception and fall through to directory-specific handling code
                } else {
                    throw new CannotDeleteFileException(path, e);
                }
            }

            //if we get this far, the path is a directory and we need to try to recursively delete its children
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path child : stream) {
                    rm(child);
                }
            } catch (IOException e) {
                throw new CannotDeleteFileException(path, e);
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
     * Deletes a path.
     * <p>
     * If the given path is a directory, then the directory's contents will first be deleted recursively and in parallel across multiple threads. This
     * may be faster than {@link #rm(Path)} for deleting large directory trees.
     *
     * @param path the path to be deleted
     * @throws CannotDeleteFileException if the path cannot be deleted
     */
    public void rmParallel(@NonNull Path path) throws CannotDeleteFileException {
        try {
            if (false) {
                //trick the compiler into letting us catch ExecutionException, which is thrown unsafely
                throw new ExecutionException(null);
            }

            while (true) {
                try {
                    //we don't care about the return value - it can either be true, indicating that it was deleted, or false, indicating that it didn't exist
                    //  in the first place.
                    Files.deleteIfExists(path);
                    return;
                } catch (DirectoryNotEmptyException e) {
                    //the path is a non-empty directory, swallow exception and fall through to directory-specific handling code
                } catch (IOException e) {
                    //the implementation doesn't HAVE to throw DirectoryNotEmptyException, so we check here just in case
                    if (Files.isDirectory(path)) {
                        //the path is a non-empty directory, swallow exception and fall through to directory-specific handling code
                    } else {
                        throw new CannotDeleteFileException(path, e);
                    }
                }

                //if we get this far, the path is a directory and we need to try to recursively delete its children
                try (Stream<Path> stream = Files.list(path).parallel()) {
                    stream.forEach(PFiles::rmParallel);
                } catch (IOException e) {
                    throw new CannotDeleteFileException(path, e);
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
    public File rmContents(@NonNull File directory) throws NotADirectoryException, CannotDeleteFileException {
        if (checkDirectoryExists(directory)) {
            File[] files;
            while ((files = directory.listFiles()) != null && files.length != 0) {
                for (File f : files) {
                    rm(f);
                }
            }
        }
        return directory;
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
    public Path rmContents(@NonNull Path directory) throws NotADirectoryException, CannotDeleteFileException {
        if (checkDirectoryExists(directory)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                for (Path child : stream) {
                    rm(child);
                }
            } catch (IOException e) {
                throw new CannotDeleteFileException(directory, e);
            }
        }
        return directory;
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
    public File rmContentsParallel(@NonNull File directory) throws NotADirectoryException, CannotDeleteFileException {
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
        return directory;
    }

    /**
     * Recursively deletes all contents of a given directory.
     * <p>
     * Contents will be deleted in parallel across multiple threads. This may be faster than {@link #rmContents(Path)} for deleting large directory trees.
     * <p>
     * If the given directory does not exist, nothing will be done.
     *
     * @param directory the directory whose contents are to be deleted
     * @throws NotADirectoryException    if the given path exists, but is not a directory
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    public Path rmContentsParallel(@NonNull Path directory) throws NotADirectoryException, CannotDeleteFileException {
        if (checkDirectoryExists(directory)) {
            try {
                if (false) {
                    //trick the compiler into letting us catch ExecutionException, which is thrown unsafely
                    throw new ExecutionException(null);
                }

                try (Stream<Path> stream = Files.list(directory).parallel()) {
                    stream.forEach(PFiles::rmParallel);
                } catch (IOException e) {
                    throw new CannotDeleteFileException(directory, e);
                }
            } catch (ExecutionException e) {
                if (e.getCause() instanceof CannotDeleteFileException) {
                    throw (CannotDeleteFileException) e.getCause();
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
        return directory;
    }
}
