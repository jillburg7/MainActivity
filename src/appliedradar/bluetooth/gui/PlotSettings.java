package appliedradar.bluetooth.gui;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;

public class PlotSettings {// extends GraphicalView {


//	public PlotSettings(Context context, AbstractChart chart) {
////		super(context, chart);
//		// TODO Auto-generated constructor stub
//	}

	/** The zoom buttons rectangle. */
	private RectF mZoomR = new RectF();


	/**
	 * sampling frequency in Hz
	 */
	private final int fs = 22050; // 44100;	// put in settings so user can change

	/**
	 * last x-value, (starting x-value = 0)
	 */
	private final int endValue = fs/2;


	public XYMultipleSeriesDataset getFrequencyAxis(double[] fftData) {
		XYMultipleSeriesDataset rangeData = new  XYMultipleSeriesDataset();

		String[] titles  = new String[] { "FFT data" }; 	// titles of data
		List<double[]> xAxis = new ArrayList<double[]>(1);	// x-axis values
		List<double[]> values = new ArrayList<double[]>(1);	// y-axis values

		// Need to have just as many x values as y values
		xAxis.add(new double[fftData.length/2]);	
		values.add(new double[fftData.length/2]);

		// Linearly-spaced x-axis values for data points, increment must be constant 
		double increment = (endValue)/fftData.length;	// half of sampling freq/length of data
		for(int i =0; i < (fftData.length/2); i++) {
			xAxis.get(0)[i] = i * increment;
			values.get(0)[i] = fftData[i];
		}

		Log.i("PlotSettings", "length of dataB4 = " + fftData.length);
		Log.i("PlotSettings", "length of dataFFT = " + values.get(0).length);
		// for the buildDataset method call:
		rangeData = dataBuilder(titles, xAxis, values);
		return rangeData;
	}

	private XYMultipleSeriesDataset dataBuilder(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		addXYSeries(dataset1, titles, xValues, yValues, 0);
		return dataset1;
	}

	private void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
			List<double[]> xValues, List<double[]> yValues, int scale) {

		int length = titles.length;	// # of series to add to plot
		for (int i = 0; i < length; i++) {

			// maps the x-axis values to their respective y-axis values to add to the plot
			double[] xV = xValues.get(i);
			XYSeries series = new XYSeries(titles[i], scale);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}



/*	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//canvas.getClipBounds(mRect);
//		int top = mRect.top;
//		int left = mRect.left;
//		int width = mRect.width();
//		int height = mRect.height();
//		if (mRenderer.isInScroll()) {
//			top = 0;
//			left = 0;
//			width = getMeasuredWidth();
//			height = getMeasuredHeight();
//		}
		mChart.draw(canvas, left, top, width, height, mPaint);
		if (mRenderer != null && mRenderer.isZoomEnabled() && mRenderer.isZoomButtonsVisible()) {
			mPaint.setColor(ZOOM_BUTTONS_COLOR);
			zoomSize = Math.max(zoomSize, Math.min(width, height) / 7);
			mZoomR.set(left + width - zoomSize * 3, top + height - zoomSize * 0.775f, left + width, top
					+ height);
			canvas.drawRoundRect(mZoomR, zoomSize / 3, zoomSize / 3, mPaint);
			float buttonY = top + height - zoomSize * 0.625f;
			canvas.drawBitmap(zoomInImage, left + width - zoomSize * 2.75f, buttonY, null);
			canvas.drawBitmap(zoomOutImage, left + width - zoomSize * 1.75f, buttonY, null);
			canvas.drawBitmap(fitZoomImage, left + width - zoomSize * 0.75f, buttonY, null);
		}
		mDrawn = true;
	}*/


	/**
	 * Default dataset for initial app start-up
	 * 	-> When no data has been selected to plot & process
	 * @return myDataSet
	 */
	public XYMultipleSeriesDataset getMyDefaultData() {
		XYMultipleSeriesDataset myDataset = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries(" ");
		myDataset.addSeries(dataSeries);
		return myDataset;
	}


	/**
	 *  Default Renderer to display when no data has been selected to process (blank graph)
	 */
	public XYMultipleSeriesRenderer getMyDefaultRenderer() {

		XYSeriesRenderer r1 = new XYSeriesRenderer();
		r1.setColor(Color.BLUE);
		r1.setLineWidth(2);
		r1.setPointStyle(PointStyle.SQUARE); // CIRCLE, DIAMOND , POINT, TRIANGLE, X									
		r1.setFillPoints(true); // not for point or x don't know how to set point size or point color

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
		myRenderer.addSeriesRenderer(r1);
		myRenderer.setPanEnabled(true, true);
		myRenderer.setZoomEnabled(true, true);
		myRenderer.setZoomButtonsVisible(true);

		myRenderer.setChartTitle("FMCW Radar Data Plot");
		myRenderer.setChartTitleTextSize(30);

		myRenderer.setLegendTextSize(20);

		myRenderer.setZoomRate((float) 1.5);

		myRenderer.setAxesColor(Color.BLACK);
		myRenderer.getXLabelsAlign();
		myRenderer.setXLabelsColor(Color.BLACK);
		myRenderer.setYLabelsColor(0, Color.BLACK);
		myRenderer.setShowAxes(true);
		myRenderer.setLabelsColor(Color.BLACK);

		myRenderer.setXTitle("Samples");
		myRenderer.setYTitle("Amplitude");
		myRenderer.setAxisTitleTextSize(20);

		myRenderer.setApplyBackgroundColor(true);
		myRenderer.setBackgroundColor(Color.LTGRAY); 

		myRenderer.setMarginsColor(Color.WHITE); 

		myRenderer.setGridColor(Color.DKGRAY);
		myRenderer.setXLabels(20);
		myRenderer.setYLabels(9);
		myRenderer.setShowGrid(true);
		myRenderer.setMargins(new int[] {35, 50, 15, 30});
		return myRenderer;
	}

	/**
	 * 
	 * @return renderer
	 */
	public XYMultipleSeriesRenderer getFFTRenderer() {

		XYSeriesRenderer r2 = new XYSeriesRenderer();
		r2.setColor(Color.RED);
		r2.setLineWidth(2);
		r2.setPointStyle(PointStyle.SQUARE);

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
		myRenderer.addSeriesRenderer(r2);
		myRenderer.setPanEnabled(true, true);
		myRenderer.setZoomEnabled(true, true);
		myRenderer.setZoomButtonsVisible(true);

		myRenderer.setChartTitle("FMCW Radar Data Plot");
		myRenderer.setChartTitleTextSize(30);

		myRenderer.setLegendTextSize(20);

		myRenderer.setZoomRate((float) 1.5);

		myRenderer.setAxesColor(Color.BLACK);
		myRenderer.getXLabelsAlign();
		myRenderer.setXLabelsColor(Color.BLACK);
		myRenderer.setYLabelsColor(0, Color.BLACK);
		myRenderer.setShowAxes(true);
		myRenderer.setLabelsColor(Color.BLACK);

		myRenderer.setXTitle("Range (meters)");
		myRenderer.setYTitle("Power (dB)");
		myRenderer.setAxisTitleTextSize(20);

		// background color of the PLOT ONLY
		myRenderer.setApplyBackgroundColor(true);
		myRenderer.setBackgroundColor(Color.LTGRAY); 

		// sets the background area of the object itself
		// does not change the plots background
		myRenderer.setMarginsColor(Color.WHITE); 

		myRenderer.setGridColor(Color.DKGRAY);
		myRenderer.setXLabels(20);
		myRenderer.setYLabels(9);
		myRenderer.setShowGrid(true);
		myRenderer.setMargins(new int[] {35, 50, 15, 30});

		// Minimum & Max values to view plot area
		myRenderer.setXAxisMin(0);
		//myRenderer.setYAxisMin(0);

		return myRenderer;
	}

}
