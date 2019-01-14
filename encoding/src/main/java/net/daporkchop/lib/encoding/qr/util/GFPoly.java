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

package net.daporkchop.lib.encoding.qr.util;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class GFPoly {
    protected final GF field;
    protected final int[] coefficients;

    public GFPoly(@NonNull GF field, @NonNull int[] coefficients) {
        if (coefficients.length == 0) {
            throw new IllegalArgumentException();
        }
        this.field = field;
        int coefficientsLength = coefficients.length;
        if (coefficientsLength > 1 && coefficients[0] == 0) {
            // Leading term must be non-zero for anything except the constant polynomial "0"
            int firstNonZero = 1;
            while (firstNonZero < coefficientsLength && coefficients[firstNonZero] == 0) {
                firstNonZero++;
            }
            if (firstNonZero == coefficientsLength) {
                this.coefficients = new int[]{0};
            } else {
                this.coefficients = new int[coefficientsLength - firstNonZero];
                System.arraycopy(coefficients,
                        firstNonZero,
                        this.coefficients,
                        0,
                        this.coefficients.length);
            }
        } else {
            this.coefficients = coefficients;
        }
    }

    public int getDegree() {
        return coefficients.length - 1;
    }

    public boolean isZero() {
        return coefficients[0] == 0;
    }

    public int getCoefficient(int degree) {
        return coefficients[coefficients.length - 1 - degree];
    }

    public int evaluateAt(int a) {
        if (a == 0) {
            return getCoefficient(0);
        }
        if (a == 1) {
            int result = 0;
            for (int coefficient : coefficients) {
                result = GF.addOrSubtract(result, coefficient);
            }
            return result;
        }
        int result = coefficients[0];
        int size = coefficients.length;
        for (int i = 1; i < size; i++) {
            result = GF.addOrSubtract(field.multiply(a, result), coefficients[i]);
        }
        return result;
    }

    public GFPoly addOrSubtract(GFPoly other) {
        if (!field.equals(other.field)) {
            throw new IllegalArgumentException("GFPolys do not have same GenericGF field");
        }
        if (isZero()) {
            return other;
        }
        if (other.isZero()) {
            return this;
        }

        int[] smallerCoefficients = this.coefficients;
        int[] largerCoefficients = other.coefficients;
        if (smallerCoefficients.length > largerCoefficients.length) {
            int[] temp = smallerCoefficients;
            smallerCoefficients = largerCoefficients;
            largerCoefficients = temp;
        }
        int[] sumDiff = new int[largerCoefficients.length];
        int lengthDiff = largerCoefficients.length - smallerCoefficients.length;
        // Copy high-order terms only found in higher-degree polynomial's coefficients
        System.arraycopy(largerCoefficients, 0, sumDiff, 0, lengthDiff);

        for (int i = lengthDiff; i < largerCoefficients.length; i++) {
            sumDiff[i] = GF.addOrSubtract(smallerCoefficients[i - lengthDiff], largerCoefficients[i]);
        }

        return new GFPoly(field, sumDiff);
    }

    public GFPoly multiply(GFPoly other) {
        if (!field.equals(other.field)) {
            throw new IllegalArgumentException("GFPolys do not have same GF field");
        }
        if (isZero() || other.isZero()) {
            return field.getZero();
        }
        int[] aCoefficients = this.coefficients;
        int aLength = aCoefficients.length;
        int[] bCoefficients = other.coefficients;
        int bLength = bCoefficients.length;
        int[] product = new int[aLength + bLength - 1];
        for (int i = 0; i < aLength; i++) {
            int aCoeff = aCoefficients[i];
            for (int j = 0; j < bLength; j++) {
                product[i + j] = GF.addOrSubtract(product[i + j], field.multiply(aCoeff, bCoefficients[j]));
            }
        }
        return new GFPoly(field, product);
    }

    public GFPoly multiply(int scalar) {
        if (scalar == 0) {
            return field.getZero();
        }
        if (scalar == 1) {
            return this;
        }
        int size = coefficients.length;
        int[] product = new int[size];
        for (int i = 0; i < size; i++) {
            product[i] = field.multiply(coefficients[i], scalar);
        }
        return new GFPoly(field, product);
    }

    public GFPoly multiplyByMonomial(int degree, int coefficient) {
        if (degree < 0) {
            throw new IllegalArgumentException();
        }
        if (coefficient == 0) {
            return field.getZero();
        }
        int size = coefficients.length;
        int[] product = new int[size + degree];
        for (int i = 0; i < size; i++) {
            product[i] = field.multiply(coefficients[i], coefficient);
        }
        return new GFPoly(field, product);
    }

    public GFPoly[] divide(GFPoly other) {
        if (!field.equals(other.field)) {
            throw new IllegalArgumentException("GFPolys do not have same GF field");
        }
        if (other.isZero()) {
            throw new IllegalArgumentException("Divide by 0");
        }

        GFPoly quotient = field.getZero();
        GFPoly remainder = this;

        int denominatorLeadingTerm = other.getCoefficient(other.getDegree());
        int inverseDenominatorLeadingTerm = field.inverse(denominatorLeadingTerm);

        while (remainder.getDegree() >= other.getDegree() && !remainder.isZero()) {
            int degreeDifference = remainder.getDegree() - other.getDegree();
            int scale = field.multiply(remainder.getCoefficient(remainder.getDegree()), inverseDenominatorLeadingTerm);
            GFPoly term = other.multiplyByMonomial(degreeDifference, scale);
            GFPoly iterationQuotient = field.buildMonomial(degreeDifference, scale);
            quotient = quotient.addOrSubtract(iterationQuotient);
            remainder = remainder.addOrSubtract(term);
        }

        return new GFPoly[]{quotient, remainder};
    }
}