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

package example.crypto;

import lombok.NonNull;
import net.daporkchop.lib.common.function.VoidFunction;
import net.daporkchop.lib.crypto.keygen.EntropyPool;
import net.daporkchop.lib.encoding.Hexadecimal;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public class EntropyGatherer {
    public static void main(String... args) {
        JFrame frame = new JFrame("PorkLib - Entropy Gatherer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        AtomicReference<EntropyPool> poolRef = new AtomicReference<>(new EntropyPool(1024L));

        {
            JLabel label = new JLabel();
            label.setText("Entropy pool size");
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            frame.add(label, constraints);
        }
        {
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(1024, 16, 1073741824, 1));
            spinner.addChangeListener(event -> poolRef.updateAndGet(pool -> {
                pool.close();
                return new EntropyPool((Integer) spinner.getValue());
            }));
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridwidth = 7;
            constraints.gridx = 1;
            constraints.gridy = 0;
            frame.add(spinner, constraints);
        }

        {
            JLabel label = new JLabel();
            label.setText("Output size");
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 2;
            frame.add(label, constraints);
        }
        IntSupplier sizeGetter;
        {
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(16, 1, 1 << 24, 1));
            sizeGetter = () -> (Integer) spinner.getValue();
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridwidth = 7;
            constraints.gridx = 1;
            constraints.gridy = 2;
            frame.add(spinner, constraints);
        }

        {
            JLabel label = new JLabel();
            label.setText("Seed");
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 3;
            frame.add(label, constraints);
        }
        LongSupplier seedGetter;
        {
            JSpinner spinner = new JSpinner(new SpinnerNumberModel((int) (System.currentTimeMillis() & 0x7FFFFFFF), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
            seedGetter = () -> (Integer) spinner.getValue();
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridwidth = 7;
            constraints.gridx = 1;
            constraints.gridy = 3;
            frame.add(spinner, constraints);
        }

        {
            JLabel label = new JLabel();
            label.setText("Input");
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 4;
            frame.add(label, constraints);
        }
        VoidFunction resetInput;
        Supplier<String> inputGetter;
        {
            TextArea textArea = new TextArea();
            resetInput = () -> textArea.setText("");
            inputGetter = textArea::getText;
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridwidth = 7;
            constraints.gridheight = 5;
            constraints.gridx = 1;
            constraints.gridy = 4;
            frame.add(textArea, constraints);
        }

        {
            JButton button = new JButton("Get randomness");
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(@NonNull MouseEvent e) {
                    poolRef.get().update(inputGetter.get().getBytes(StandardCharsets.UTF_8));
                    String s = Hexadecimal.encode(poolRef.get().get(sizeGetter.getAsInt(), new Random(seedGetter.getAsLong())));
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
                    System.out.printf("Generated: %s\n", s);
                    JOptionPane.showMessageDialog(null, "Copied to clipboard!");
                }
            });
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridwidth = 4;
            constraints.gridx = 0;
            constraints.gridy = 1;
            frame.add(button, constraints);
        }
        {
            JButton button = new JButton("Reset");
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(@NonNull MouseEvent e) {
                    poolRef.get().clear();
                    resetInput.run();
                }
            });
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridwidth = 4;
            constraints.gridx = 4;
            constraints.gridy = 1;
            frame.add(button, constraints);
        }

        frame.pack();
        frame.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> poolRef.get().close()));
    }
}
