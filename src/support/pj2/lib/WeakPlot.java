//******************************************************************************
//
// File:    WeakPlot.java
// Package: ---
// Unit:    Class WeakPlot
//
// This Java source file is copyright (C) 2013 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the Parallel Java 2 Library ("PJ2"). PJ2 is
// free software; you can redistribute it and/or modify it under the terms of
// the GNU General Public License as published by the Free Software Foundation;
// either version 3 of the License, or (at your option) any later version.
//
// PJ2 is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

import edu.rit.numeric.ListXYSeries;
import edu.rit.numeric.plot.Dots;
import edu.rit.numeric.plot.Plot;
import edu.rit.numeric.plot.Strokes;
import java.awt.Color;
import java.io.File;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Scanner;

/**
 * Class WeakPlot is a program that creates plots of a parallel program's
 * performance under weak scaling, as calculated from measured running time
 * data. The WeakPlot program also prints the plotted data on the standard
 * output. Weak scaling of a parallel program is when you increase the problem
 * size in proportion to the number of cores you add, so you expect the running
 * time to stay the same (a sizeup).
 * <P>
 * Usage: <TT>java WeakPlot <I>file</I></TT>
 * <P>
 * Each line of the WeakPlot program's input file contains the following data.
 * Each data item is separated from the next by whitespace. Blank lines are
 * ignored.
 * <UL>
 * <P><LI>
 * Problem size label <I>N</I>. This is a string denoting the problem size. The
 * string uses URL encoding, as explained in class {@linkplain
 * java.net.URLEncoder URLEncoder}.
 * <P><LI>
 * Number of cores <I>K</I>. This is either 0 denoting a sequential program run,
 * or a number &ge; 1 denoting a parallel program run on <I>K</I> cores.
 * <P><LI>
 * One or more running times <I>T</I> in milliseconds. These give the measured
 * running time of the program on <I>K</I> cores on a problem of <I>K</I> times
 * the given problem size.
 * </UL>
 * <P>
 * The WeakPlot program determines the running time <I>T</I>(<I>KN,K</I>)
 * to be the minimum <I>T</I> value on the input line for <I>N</I> and <I>K</I>.
 * <P>
 * The WeakPlot program generates the following plots. Each plot includes
 * one data series for each problem size label <I>N</I>.
 * <UL>
 * <P><LI>
 * Parallel program's running time <I>T</I> versus <I>K</I> for each <I>N</I>. A
 * log-log plot.
 * <P><LI>
 * Parallel program's sizeup versus <I>K</I> for each <I>N</I>. Sizeup is the
 * sequential program's running time on one core divided by the parallel
 * program's running time on <I>K</I> cores, times <I>K</I>.
 * <P><LI>
 * Parallel program's sizeup efficiency versus <I>K</I> for each <I>N</I>.
 * Sizeup efficiency is the sequential program's running time on one core
 * divided by the parallel program's running time on <I>K</I> cores.
 * <P><LI>
 * Parallel program's weak sequential fraction <I>G</I> versus <I>K</I> for each
 * <I>N</I>. <I>G</I> is determined from the Weak Scaling Law:
 * <P><CENTER>
 * <I>T</I>(<I>KN,K</I>) = <I>K</I>&sdot;<I>G</I>&sdot;<I>T</I>(<I>N,</I>1) + (1 &minus; <I>G</I>)&sdot;<I>T</I>(<I>N,</I>1)
 * </CENTER>
 * </UL>
 *
 * @author  Alan Kaminsky
 * @version 29-Jun-2013
 */
public class WeakPlot
	{

// Prevent construction.

	private WeakPlot()
		{
		}

// Hidden helper classes.

	private static class Key
		{
		public String N;
		public int K;
		public Key (String N, int K)
			{
			this.N = N;
			this.K = K;
			}
		public boolean equals (Object obj)
			{
			return (obj instanceof Key) &&
				((Key) obj).N.equals (this.N) &&
				((Key) obj).K == this.K;
			}
		public int hashCode()
			{
			return N.hashCode()*31 + K;
			}
		}

// Main program.

	/**
	 * Main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		Key key;

		// Parse command line arguments.
		if (args.length != 1) usage();
		File file = new File (args[0]);

		// Mapping from (N,K) to T.
		HashMap<Key,Long> dataMap = new HashMap<Key,Long>();

		// Set of problem sizes.
		LinkedHashSet<String> problemSizeSet = new LinkedHashSet<String>();

		// Largest K value encountered.
		int Kmax = -1;

		// Read input file.
		Scanner scanner = new Scanner (file);
		while (scanner.hasNextLine())
			{
			Scanner linescanner = new Scanner (scanner.nextLine());
			if (! linescanner.hasNext()) continue;
			String N = URLDecoder.decode (linescanner.next(), "UTF-8");
			if (! linescanner.hasNextInt())
				throw new IllegalArgumentException (String.format
					("K missing for N=\"%s\"", N));
			int K = linescanner.nextInt();
			Kmax = Math.max (Kmax, K);
			long Tmin = Long.MAX_VALUE;
			while (linescanner.hasNextLong())
				Tmin = Math.min (Tmin, linescanner.nextLong());
			if (Tmin == Long.MAX_VALUE)
				throw new IllegalArgumentException (String.format
					("No T data for N=\"%s\", K=%d", N, K));
			dataMap.put (new Key (N, K), Tmin);
			problemSizeSet.add (N);
			}

		// Make sure sequential program data is there.
		for (String N : problemSizeSet)
			{
			key = new Key (N, 0);
			if (! dataMap.containsKey (key))
				throw new IllegalArgumentException (String.format
					("No data for N=\"%s\", K=0", N));
			key = new Key (N, 1);
			if (! dataMap.containsKey (key))
				throw new IllegalArgumentException (String.format
					("No data for N=\"%s\", K=1", N));
			}

		// Print data.
		for (String N : problemSizeSet)
			{
			System.out.printf ("N\tK\tT\tSizup\tSzEff\tSeqFr%n");
			key = new Key (N, 0);
			long T_0 = dataMap.get (key);
			System.out.printf ("%s\tseq\t%d%n", N, T_0);
			key = new Key (N, 1);
			long T_1 = dataMap.get (key);
			System.out.printf ("\t1\t%d\t%.3f\t%.3f%n", T_1,
				sizup (T_0, T_1, 1), szeff (T_0, T_1));
			for (int K = 2; K <= Kmax; ++ K)
				{
				key = new Key (N, K);
				if (dataMap.containsKey (key))
					{
					long T_K = dataMap.get (key);
					System.out.printf ("\t%d\t%d\t%.3f\t%.3f\t%.3f%n", K, T_K,
						sizup (T_0, T_K, K), szeff (T_0, T_K),
						seqfr (T_1, T_K, K));
					}
				}
			System.out.println();
			}

		// Generate running time plot.
		Plot runningTimePlot = new Plot()
			.plotTitle ("Running Time vs. Cores")
			.rightMargin (54)
			.majorGridLines (true)
			.minorGridLines (true)
			.xAxisKind (Plot.LOGARITHMIC)
			.xAxisMinorDivisions (10)
			.xAxisTitle ("Cores")
			.yAxisKind (Plot.LOGARITHMIC)
			.yAxisMinorDivisions (10)
			.yAxisTitle ("Running time (sec)")
			.yAxisTickFormat (new DecimalFormat ("0E0"))
			.labelPosition (Plot.RIGHT)
			.labelOffset (6);
		for (String N : problemSizeSet)
			{
			ListXYSeries series = new ListXYSeries();
			for (int K = 1; K <= Kmax; ++ K)
				{
				key = new Key (N, K);
				if (dataMap.containsKey (key))
					series.add (K, dataMap.get(key)/1000.0);
				}
			runningTimePlot
				.xySeries (series)
				.label (N, series.x (series.length() - 1),
					series.y (series.length() - 1));
			}

		// Generate sizeup plot.
		Plot sizeupPlot = new Plot()
			.plotTitle ("Sizeup vs. Cores")
			.rightMargin (54)
			.majorGridLines (true)
			.xAxisTitle ("Cores")
			.yAxisTitle ("Sizeup")
			.labelPosition (Plot.RIGHT)
			.labelOffset (6)
			.seriesDots (null)
			.seriesColor (Color.RED)
			.xySeries (0, 0, Kmax, Kmax)
			.seriesDots (Dots.circle())
			.seriesColor (Color.BLACK);
		for (String N : problemSizeSet)
			{
			ListXYSeries series = new ListXYSeries();
			key = new Key (N, 0);
			long T_0 = dataMap.get (key);
			for (int K = 1; K <= Kmax; ++ K)
				{
				key = new Key (N, K);
				if (dataMap.containsKey (key))
					series.add (K, sizup (T_0, dataMap.get(key), K));
				}
			sizeupPlot
				.xySeries (series)
				.label (N, series.x (series.length() - 1),
					series.y (series.length() - 1));
			}

		// Generate sizeup efficiency plot.
		Plot efficiencyPlot = new Plot()
			.plotTitle ("Sizeup Efficiency vs. Cores")
			.rightMargin (54)
			.majorGridLines (true)
			.xAxisTitle ("Cores")
			.yAxisTitle ("Sizeup Efficiency")
			.yAxisTickFormat (new DecimalFormat ("0.0"))
			.labelPosition (Plot.RIGHT)
			.labelOffset (6)
			.seriesDots (null)
			.seriesColor (Color.RED)
			.xySeries (0, 1, Kmax, 1)
			.seriesDots (Dots.circle())
			.seriesColor (Color.BLACK);
		for (String N : problemSizeSet)
			{
			ListXYSeries series = new ListXYSeries();
			key = new Key (N, 0);
			long T_0 = dataMap.get (key);
			for (int K = 1; K <= Kmax; ++ K)
				{
				key = new Key (N, K);
				if (dataMap.containsKey (key))
					series.add (K, szeff (T_0, dataMap.get(key)));
				}
			efficiencyPlot
				.xySeries (series)
				.label (N, series.x (series.length() - 1),
					series.y (series.length() - 1));
			}

		// Generate weak sequential fraction plot.
		Plot fractionPlot = new Plot()
			.plotTitle ("Weak Sequential Fraction vs. Cores")
			.rightMargin (54)
			.majorGridLines (true)
			.xAxisTitle ("Cores")
			.yAxisTitle ("Weak sequential fraction \u00d71000")
			.labelPosition (Plot.RIGHT)
			.labelOffset (6);
		for (String N : problemSizeSet)
			{
			ListXYSeries series = new ListXYSeries();
			key = new Key (N, 1);
			long T_1 = dataMap.get (key);
			for (int K = 2; K <= Kmax; ++ K)
				{
				key = new Key (N, K);
				if (dataMap.containsKey (key))
					series.add (K, seqfr (T_1, dataMap.get(key), K)*1000);
				}
			fractionPlot
				.xySeries (series)
				.label (N, series.x (series.length() - 1),
					series.y (series.length() - 1));
			}

		// Display plots.
		runningTimePlot.getFrame().setVisible (true);
		sizeupPlot.getFrame().setVisible (true);
		efficiencyPlot.getFrame().setVisible (true);
		fractionPlot.getFrame().setVisible (true);
		}

// Hidden operations.

	/**
	 * Compute sizeup efficiency.
	 */
	private static double szeff
		(double T_0,
		 double T_K)
		{
		return T_0/T_K;
		}

	/**
	 * Compute sizeup.
	 */
	private static double sizup
		(double T_0,
		 double T_K,
		 int K)
		{
		return szeff(T_0,T_K)*K;
		}

	/**
	 * Compute sequential fraction.
	 */
	private static double seqfr
		(double T_1,
		 double T_K,
		 int K)
		{
		return (T_K - T_1)/T_1/(K - 1);
		}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java WeakPlot <file>");
		System.exit (1);
		}

	}
