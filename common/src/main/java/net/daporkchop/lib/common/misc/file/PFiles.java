/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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
import net.daporkchop.lib.common.util.exception.file.CannotCreateDirectoryException;
import net.daporkchop.lib.common.util.exception.file.CannotCreateFileException;
import net.daporkchop.lib.common.util.exception.file.CannotDeleteFileException;
import net.daporkchop.lib.common.util.exception.file.NotADirectoryException;
import net.daporkchop.lib.common.util.exception.file.NotAFileException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

/**
 * Helper methods for working with files.
 *
 * @author DaPorkchop_
 */
public interface PFiles {
    /**
     * Ensures that a directory exists and is a directory, creating a new directory if it doesn't exist and throwing an exception in case
     * of failure.
     *
     * @param directory the directory to ensure the existence of
     * @return the directory
     * @throws CannotCreateDirectoryException if the directory could not be created
     * @throws NotADirectoryException         if the given path is not a directory
     */
    static File ensureDirectoryExists(@NonNull File directory) throws CannotCreateDirectoryException, NotADirectoryException {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new CannotCreateDirectoryException(directory);
        } else if (!directory.isDirectory()) {
            throw new NotADirectoryException(directory);
        } else {
            return directory;
        }
    }

    /**
     * Ensures that a file exists and is a file, creating a new file and all required parent directories if it doesn't exist and throwing
     * and exception in case of failure.
     *
     * @param file the file to ensure the existence of
     * @return the file
     * @throws CannotCreateFileException      if the file could not be created
     * @throws CannotCreateDirectoryException if the file's parent directory could not be created
     * @throws NotAFileException              if the given path is not a file
     * @throws NotADirectoryException         if the given path's parent file is not a directory
     */
    static File ensureFileExists(@NonNull File file) throws CannotCreateFileException, CannotCreateDirectoryException, NotAFileException, NotADirectoryException {
        if (!file.exists()) {
            ensureDirectoryExists(file.getParentFile()); //TODO: how will this work in a filesystem root? not that we should ever be creating files there...
            try {
                if (!file.createNewFile()) {
                    throw new CannotCreateFileException(file);
                }
            } catch (IOException e) {
                throw new CannotCreateFileException(file, e);
            }
        } else if (!file.isFile()) {
            throw new NotAFileException(file);
        }
        return file;
    }

    /**
     * Recursively delete a file.
     *
     * @param file the file to be deleted
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    static void rm(@NonNull File file) throws CannotDeleteFileException {
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
     * Recursively deletes a file on multiple threads, may be faster for deleting large directory trees.
     *
     * @param file the file to be deleted
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    static void rmParallel(@NonNull File file) throws CannotDeleteFileException {
        try {
            if (false) {
                //trick the compiler into letting us catch ExecutionException, which is thrown unsafely
                throw new ExecutionException(null);
            }
            while (file.exists()) {
                if (file.isDirectory()) {
                    File[] files;
                    while ((files = file.listFiles()) != null && files.length != 0) {
                        StreamSupport.stream(Arrays.spliterator(files), true).forEach(f -> rmParallel(file));
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
     * Recursively deletes all subfiles of a given directory.
     *
     * @param file the directory whose contents are to be deleted
     * @throws NotADirectoryException    if the given file is not a directory
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    static void rmContents(@NonNull File file) throws NotADirectoryException, CannotDeleteFileException {
        if (file.exists() && !file.isDirectory()) {
            throw new NotADirectoryException(file);
        } else {
            File[] files;
            while ((files = file.listFiles()) != null && files.length != 0) {
                for (File f : files) {
                    rm(f);
                }
            }
        }
    }

    /**
     * Recursively deletes all subfiles of a given directory on multiple threads, may be faster for deleting large directory trees.
     *
     * @param file the directory whose contents are to be deleted
     * @throws NotADirectoryException    if the given file is not a directory
     * @throws CannotDeleteFileException if the file cannot be deleted
     */
    static void rmContentsParallel(@NonNull File file) throws NotADirectoryException, CannotDeleteFileException {
        if (file.exists() && !file.isDirectory()) {
            throw new NotADirectoryException(file);
        } else {
            try {
                if (false) {
                    //trick the compiler into letting us catch ExecutionException, which is thrown unsafely
                    throw new ExecutionException(null);
                }
                File[] files;
                while ((files = file.listFiles()) != null && files.length != 0) {
                    StreamSupport.stream(Arrays.spliterator(files), true).forEach(f -> rmParallel(file));
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
