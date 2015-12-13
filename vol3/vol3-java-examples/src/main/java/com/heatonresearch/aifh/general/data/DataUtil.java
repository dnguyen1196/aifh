/*
 * Artificial Intelligence for Humans
 * Volume 3: Deep Learning and Neural Networks
 * Java Version
 * http://www.aifh.org
 * http://www.jeffheaton.com
 *
 * Code repository:
 * https://github.com/jeffheaton/aifh
 *
 * Copyright 2014-2015 by Jeff Heaton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information on Heaton Research copyrights, licenses
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package com.heatonresearch.aifh.general.data;

import au.com.bytecode.opencsv.CSVWriter;
import com.heatonresearch.aifh.error.ErrorCalculation;
import com.heatonresearch.aifh.learning.ClassificationAlgorithm;
import com.heatonresearch.aifh.learning.RegressionAlgorithm;
import com.heatonresearch.aifh.randomize.GenerateRandom;
import com.heatonresearch.aifh.randomize.MersenneTwisterGenerateRandom;
import com.heatonresearch.aifh.util.ArrayUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Several dataset utilities.
 */
public class DataUtil {
    /**
     * Split a list into two sublists by randomly shuffling the values (without replacement).
     * @param list The list to split/shuffle.
     * @param ratio The size of the first retuerned list.
     * @param rnd A random number generator to split the lists.
     * @param <T> The type that the lists contain.
     * @return A list containing the two split lists.
     */
    public static <T> List<List<T>> split(final List<T> list, final double ratio, final GenerateRandom rnd) {
        List<List<T>> result = new ArrayList<>();
        int aCount = (int)(list.size() * ratio);

        List<T> a = new ArrayList<>();
        List<T> b = new ArrayList<>();
        result.add(a);
        result.add(b);

        b.addAll(list);

        for(int i=0;i<aCount;i++) {
            int idx = rnd.nextInt(0,b.size());
            a.add(b.get(idx));
            b.remove(idx);
        }

        return result;
    }

    /**
     * Split a list into two sublists by randomly shuffling the values (without replacement).
     * A new Mersenne twister random number generator will be used.
     * @param list The list to split/shuffle.
     * @param ratio The size of the first retuerned list.
     * @param <T> The type that the lists contain.
     * @return A list containing the two split lists.
     */
    public static <T> List<List<T>> split(final List<T> list, final double ratio) {
        return split(list,ratio,new MersenneTwisterGenerateRandom());
    }

    public static double calculateRegressionError(final List<BasicData> dataset,
                                                  RegressionAlgorithm model,
                                                  ErrorCalculation calc) {
        calc.clear();
        for(BasicData item: dataset) {
            double[] output = model.computeRegression(item.getInput());
            calc.updateError(output, item.getIdeal(), 1.0);
        }

        return calc.calculate();
    }

    public static double calculateClassificationError(
            List<BasicData> data,
            ClassificationAlgorithm model) {
        int total = 0;
        int correct = 0;

        for(BasicData pair : data ) {
            int ideal = ArrayUtil.indexOfLargest(pair.getIdeal());
            int actual = model.computeClassification(pair.getInput());
            if( actual==ideal )
                correct++;
            total++;
        }
        return (double)(total-correct) / (double)total;

    }

    public static void dumpCSV(File file, List<BasicData> dataset) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));
        int inputCount = dataset.get(0).getInput().length;
        int outputCount = dataset.get(0).getIdeal().length;
        int totalCount = inputCount + outputCount;

        String[] headers = new String[totalCount];
        int idx = 0;
        for(int i=0;i<inputCount;i++) {
            headers[idx++] = "x"+i;
        }
        for(int i=0;i<outputCount;i++) {
            headers[idx++] = "y"+i;
        }
        writer.writeNext(headers);

        String[] line = new String[totalCount];
        for(int i = 0; i<dataset.size(); i++) {
            BasicData item = dataset.get(i);

            idx = 0;
            for(int j=0;j<inputCount;j++) {
                line[idx++] = String.format(Locale.ENGLISH, "%.2f", item.getInput()[j]);
            }
            for(int j=0;j<outputCount;j++) {
                line[idx++] = String.format(Locale.ENGLISH, "%.2f", item.getIdeal()[j]);
            }
            writer.writeNext(line);

        }
        writer.close();
    }

}
