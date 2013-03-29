package appliedradar.bluetooth.gui;

import org.achartengine.chart.AbstractChart;

public abstract class PlotRenderer extends AbstractChart {

//	public void PlotRenderer() {
//	//	MathHelper math;
//	}
//	
//	GraphicalView mChartView;
	
	/**
	 * sampling frequency
	 */
//	private final int fs = 44100;
	
	/**
	 * last x-value, (starting x-value = 0)
	 */
//	private final int endValue = fs/2;
	
	
//	public void getFrequencyAxis(double[] fftData) {
		//
//			    double[] freqAxis = new double[endValue];
		//
//			   	
//			    
//			    // half of sampling freq/length of data
//			    for(int i =0; i < fftData.length; i++) {
//			    	double increment = (endValue)/fftData.length;
		//
		//
//			    }
//			}
	
	
	
//	public XYMultipleSeriesDataset getFrequencyAxis(double[] fftData) {
//		 XYMultipleSeriesDataset dataSet = new  XYMultipleSeriesDataset();
//		// for the buildDataset function call:
//		String[] titles = null; // = new String[];		// titles of data (legend titles)
//		List<double[]> xAxis = new ArrayList<double[]>(1);		// xValues
//	    List<double[]> values = new ArrayList<double[]>(1);		// yValues
//	    
//	    double[] freqAxis = new double[endValue];
//	    xAxis.add(freqAxis);
//	    values.add(fftData);
//	   	
//	    
//	    // half of sampling freq/length of data
//	    for(int i =0; i < fftData.length; i++) {
//	    	double increment = (endValue)/fftData.length;
//	    	xAxis.get(0)[i] = i * increment;
////	    	xAxis.get(0)[i] = fftData[i] * increment;
//	    }
//	//   dataSet = buildDataset(titles, xAxis, values);
//	   return dataSet;
//	}
	
	
	  /**
	   * Builds an XY multiple dataset using the provided values.
	   * 
	   * @param titles the series titles
	   * @param xValues the values for the X axis
	   * @param yValues the values for the Y axis
	   * @return the XY multiple dataset
	   */
//	  protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
//	      List<double[]> yValues) {
//	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//	    addXYSeries(dataset, titles, xValues, yValues, 0);
//	    return dataset;
//	  }
//	  
//	  public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
//		      List<double[]> yValues, int scale) {
//		    int length = titles.length;
//		    for (int i = 0; i < length; i++) {
//		      XYSeries series = new XYSeries(titles[i], scale);
//		      double[] xV = xValues.get(i);
//		      double[] yV = yValues.get(i);
//		      int seriesLength = xV.length;
//		      for (int k = 0; k < seriesLength; k++) {
//		        series.add(xV[k], yV[k]);
//		      }
//		      dataset.addSeries(series);
//		    }
//		  }
	  
	  /**
	   * Executes the chart demo.
	   * @param context the context
	   * @return the built intent
	   */
//	  public Intent execute(Context context) {
//	    String[] titles = new String[] { "sin", "cos" };
//	    List<double[]> x = new ArrayList<double[]>();
//	    List<double[]> values = new ArrayList<double[]>();
//	    int step = 4;
//	    int count = 360 / step + 1;
//	    x.add(new double[count]);
//	    x.add(new double[count]);
//	    double[] sinValues = new double[count];
//	    double[] cosValues = new double[count];
//	    values.add(sinValues);
//	    values.add(cosValues);
//	    for (int i = 0; i < count; i++) {
//	      int angle = i * step;
//	      x.get(0)[i] = angle;
//	      x.get(1)[i] = angle;
//	      double rAngle = Math.toRadians(angle);
//	      sinValues[i] = Math.sin(rAngle);
//	      cosValues[i] = Math.cos(rAngle);
//	    }
//	    int [] colors = new int[] { Color.BLUE, Color.CYAN };
//	    PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT };
//	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
//	    setChartSettings(renderer, "Trigonometric functions", "X (in degrees)", "Y", 0, 360, -1, 1,
//	        Color.GRAY, Color.LTGRAY);
//	    renderer.setXLabels(20);
//	    renderer.setYLabels(10);
//	    return ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values), renderer);
//	  }
	
}
