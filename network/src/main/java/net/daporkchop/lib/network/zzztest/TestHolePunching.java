/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.network.zzztest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@FunctionalInterface
interface ThrowableRunnable extends Runnable {
    @Override
    default void run() {
        try {
            this.doRun();
        } catch (Exception e) {
            if ("Socket closed".equals(e.getMessage()))
            throw new RuntimeException(e);
        }
    }

    void doRun() throws Exception;
}

/**
 * Really ugly test class while I experiment with TCP hole punching for p2p
 *
 * Only port 12347 is opened, 12348 and 12349 are both closed
 */
public class TestHolePunching {
    public static void main(String... args) {
        AtomicBoolean running = new AtomicBoolean(true);
        try (ServerSocket serverSocket = new ServerSocket()) {
            {
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(12347));
                new Thread((ThrowableRunnable) () -> {
                    while (running.get()) {
                        Socket socket = serverSocket.accept();
                        System.out.printf("Accepted connection from %s\n", socket.getRemoteSocketAddress());
                    }
                }).start();
            }
            Socket s1, s2;
            //initiate connections to server
            {
                s1 = new Socket();
                s2 = new Socket();
                s1.setReuseAddress(true);
                s2.setReuseAddress(true);
                s1.bind(new InetSocketAddress(12348));
                s2.bind(new InetSocketAddress(12349));
                s1.connect(new InetSocketAddress("home.daporkchop.net", 12347));
                s2.connect(new InetSocketAddress("home.daporkchop.net", 12347));
            }
            Thread.sleep(1000L);
            ServerSocket ss1, ss2;
            //initiate servers
            {
                //ss1 = new ServerSocket();
                ss2 = new ServerSocket();
                //ss1.setReuseAddress(true);
                ss2.setReuseAddress(true);
                //ss1.bind(new InetSocketAddress(12348));
                ss2.bind(new InetSocketAddress(12349));
                new Thread((ThrowableRunnable) () -> {
                    while (running.get()) {
                        Socket socket = ss2.accept();
                        System.out.printf("Accepted connection from %s\n", socket.getRemoteSocketAddress());
                    }
                }).start();
            }
            Thread.sleep(1000L);
            //attempt to connect
            {
                Socket s3 = new Socket();
                s3.setReuseAddress(true);
                s3.bind(new InetSocketAddress(12348));
                s3.connect(new InetSocketAddress("home.daporkchop.net", 12349));
                Thread.sleep(1000L);
                s3.close();
            }
            s1.close();
            s2.close();
            //ss1.close();
            ss2.close();
        } catch (InterruptedException e)    {
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            running.set(false);
        }
    }
}
