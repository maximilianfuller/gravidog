//******************************************************************************
//
// File:    StrongPlot.java
// Package: ---
// Unit:    Class StrongPlot
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
 * Class StrongPlot is a program that creates plots of a parallel program's
 * performance under strong scaling, as calculated from measured running time
 * data. The StrongPlot program also prints the plotted data on the standard
 * output. Strong scaling of a parallel program is when you keep the problem
 * size the same as you add more cores, so you expect the running time to
 * decrease (a speedup).
 * <P>
 * Usage: <TT>java StrongPlot <I>file</I></TT>
 * <P>
 * Each line of the StrongPlot program's input file contains the following data.
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
 * running time of the program on <I>K</I> cores on a problem of the given
 * problem size.
 * </UL>
 * <P>
 * The StrongPlot program determines the running time
 * <I>T</I>(<I>N,K</I>) to be the minimum <I>T</I> value on the input line for
 * <I>N</I> and <I>K</I>.
 * <P>
 * The StrongPlot program generates the following plots. Each plot
 * includes one data series for each problem size label <I>N</I>.
 * <UL>
 * <P><LI>
 * Parallel program's running time <I>T</I> versus <I>K</I> for each <I>N</I>. A
 * log-log plot.
 * <P><LI>
 * Parallel program's speedup versus <I>K</I> for each <I>N</I>. Speedup is the
 * sequential program's running time on one core divided by the parallel
 * program's running time on <I>K</I> cores.
 * <P><LI>
 * Parallel program's efficiency versus <I>K</I> for each <I>N</I>. Efficiency
 * is the speedup divided by <I>K</I>.
 * <P><LI>
 * Parallel program's sequential fraction <I>F</I> versus <I>K</I> for each
 * <I>N</I>. <I>F</I> is determined from Amdahl's Law:
 * <P><CENTER>
 * <I>T</I>(<I>N,K</I>) = <I>F</I>&sdot;<I>T</I>(<I>N,</I>1) + (1 &minus; <I>F</I>)&sdot;<I>T</I>(<I>N,</I>1)/<I>K</I>
 * </CENTER>
 * </UL>
 *
 * @author  Alan Kaminsky
 * @version 29-Jun-2013
 */
public class StrongPlot
	{

// Prevent construction.

	private StrongPlot()
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
			System.out.printf ("N\tK\tT\tSpdup\tEffic\tSeqFr%n");
			key = new Key (N, 0);
			long T_0 = dataMap.get (key);
			System.out.printf ("%s\tseq\t%d%n", N, T_0);
			key = new Key (N, 1);
			long T_1 = dataMap.get (key);
			System.out.printf ("\t1\t%d\t%.3f\t%.3f%n", T_1,
				spdup (T_0, T_1), effic (T_0, T_1, 1));
			for (int K = 2; K <= Kmax; ++ K)
				{
				key = new Key (N, K);
				if (dataMap.containsKey (key))
					{
					long T_K = dataMap.get (key);
					System.out.printf ("\t%d\t%d\t%.3f\t%.3f\t%.3f%n", K, T_K,
						spdup (T_0, T_K), effic (T_0, T_K, K),
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

		// Generate speedup plot.
		Plot speedupPlot = new Plot()
			.plotTitle ("Speedup vs. Cores")
			.rightMargin (54)
			.majorGridLines (true)
			.xAxisTitle ("Cores")
			.yAxisTitle ("Speedup")
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
					series.add (K, spdup (T_0, dataMap.get(key)));
				}
			speedupPlot
				.xySeries (series)
				.label (N, series.x (series.length() - 1),
					series.y (series.length() - 1));
			}

		// Generate efficiency plot.
		Plot efficiencyPlot = new Plot()
			.plotTitle ("Efficiency vs. Cores")
			.rightMargin (54)
			.majorGridLines (true)
			.xAxisTitle ("Cores")
			.yAxisTitle ("Efficiency")
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
					series.add (K, effic (T_0, dataMap.get(key), K));
				}
			efficiencyPlot
				.xySeries (series)
				.label (N, series.x (series.length() - 1),
					series.y (series.length() - 1));
			}

		// Generate sequential fraction plot.
		Plot fractionPlot = new Plot()
			.plotTitle ("Sequential Fraction vs. Cores")
			.rightMargin (54)
			.majorGridLines (true)
			.xAxisTitle ("Cores")
			.yAxisTitle ("Sequential fraction \u00d71000")
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
		speedupPlot.getFrame().setVisible (true);
		efficiencyPlot.getFrame().setVisible (true);
		fractionPlot.getFrame().setVisible (true);
		}

// Hidden operations.

	/**
	 * Compute speedup.
	 */
	private static double spdup
		(double T_0,
		 double T_K)
		{
		return T_0/T_K;
		}

	/**
	 * Compute efficiency.
	 */
	private static double effic
		(double T_0,
		 double T_K,
		 int K)
		{
		return spdup(T_0,T_K)/K;
		}

	/**
	 * Compute sequential fraction.
	 */
	private static double seqfr
		(double T_1,
		 double T_K,
		 int K)
		{
		return (K*T_K - T_1)/T_1/(K - 1);
		}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java StrongPlot <file>");
		System.exit (1);
		}

	}