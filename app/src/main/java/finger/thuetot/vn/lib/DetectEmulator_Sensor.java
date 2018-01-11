package finger.thuetot.vn.lib;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

public class DetectEmulator_Sensor implements SensorEventListener {
	private static final String TAG = "DetectSimu_Sensor";
    private boolean showLog = false;
	private SensorManager mySensorManager;
	private Sensor myProximitySensor;
	private Sensor myGSensor;
	private Sensor myLightSensor;
	private Sensor myMagneticSensor;
	private Sensor myGLSensor;
	private Sensor myGravitySensor;
	private DetectResult dr;
	private boolean proximityState = true;
	private boolean accelerometerState = true;
	private boolean lightState = true;
	private boolean magneticState = true;
	private boolean accelerometerLState = true;
	private boolean gravityState = true;
	private int totalOK = 0;
    private int tempGCount = 0;
	private int tempMCount = 0;
	private int tempGLCount = 0;
	private int tempGACount = 0;
	private float[] tempGValue = new float[3];
	private float[] tempMagValue = new float[3];
	private float[] tempGLValue = new float[3];
	private float[] tempGaValue = new float[3];
	private static final int SAMPLING_TIMES = 5;
	
	private boolean checkResult = false;

	public DetectEmulator_Sensor(Context ctx) {
	    init(ctx);
    }
	
	private void init(Context ctx){
		mySensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
		
		dr = new DetectResult(){
			@Override
			public void Result(boolean isok) {				
			}			
		};

		setShowLog(false);
	}

	public void setShowLog(boolean show) {
	    showLog = show;
    }
	
	public void Detect(DetectResult detectResult){
		this.dr = detectResult;
		
		checkResult = false;
		proximityState = true;
		accelerometerState = true;
		lightState = true;
		magneticState = true;
        accelerometerLState = true;
		gravityState = true;
		totalOK = 0;
        tempGCount = 0;
        tempMCount = 0;
        int totalFail = 0;
        tempGLCount = 0;
        tempGACount = 0;
		
		myProximitySensor = mySensorManager
				.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		if (myProximitySensor == null) {
			if (showLog) Log.i(TAG, "No Proximity Sensor!");
			proximityState = false;
			totalFail++;
		} else {
			mySensorManager.registerListener(this,
					myProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		myGSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (myGSensor == null) {
            if (showLog) Log.i(TAG,"No G Sensor!");
			accelerometerState = false;
			totalFail++;
		} else {
			mySensorManager.registerListener(this,
					myGSensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		myLightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if (myLightSensor == null) {
            if (showLog) Log.i(TAG, "No Light Sensor!");
			lightState = false;
			totalFail++;
		} else {
			mySensorManager.registerListener(this,
					myLightSensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		myMagneticSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if (myMagneticSensor == null) {
            if (showLog) Log.i(TAG, "No Magnetic Sensor!");
			magneticState = false;
			totalFail++;
		} else {
			mySensorManager.registerListener(this,
					myMagneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		myGLSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		if (myGLSensor == null) {
            if (showLog) Log.i(TAG, "No Linear Acceleration Sensor!");
            accelerometerLState = false;
			totalFail++;
		} else {
			mySensorManager.registerListener(this,
					myGLSensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		myGravitySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		if (myGravitySensor == null) {
            if (showLog) Log.i(TAG, "No Gravity Sensor!");
			gravityState = false;
			totalFail++;
		} else {
			mySensorManager.registerListener(this,
					myGravitySensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		
		if(totalFail > 5){
			stopDetect();
			dr.Result(false);
		}else{
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					if(!checkResult){
						stopDetect();
						dr.Result(false);
					}
				}			
			}, 4000);
		}		
	}
	
	private void stopDetect(){
		mySensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_PROXIMITY && proximityState) {
            if (showLog) Log.v(TAG, "pvalue:" + event.values[0]);
			if(event.values[0] != 0){
				proximityState = false;
				totalOK++;
				mySensorManager.unregisterListener(this, myProximitySensor);				
			}
		}
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && accelerometerState) {
            if (showLog) Log.v(TAG, "gvalue:" + event.values[0] + "," + event.values[1] + "," + event.values[2]);
			if(tempGValue != null){
				if(tempGValue[0] != event.values[0] && tempGValue[1] != event.values[1] && tempGValue[2] != event.values[2])tempGCount++;
				if(tempGCount >= SAMPLING_TIMES){
					accelerometerState = false;
					totalOK++;
					mySensorManager.unregisterListener(this, myGSensor);				
				}

                tempGValue[0] = event.values[0];
                tempGValue[1] = event.values[1];
                tempGValue[2] = event.values[2];
			}
		}
		
		if (event.sensor.getType() == Sensor.TYPE_LIGHT && lightState) {
            if (showLog) Log.v(TAG, "lvalue:" + event.values[0]);
			if(event.values[0] > 0){
				lightState = false;
				totalOK++;
				mySensorManager.unregisterListener(this, myLightSensor);
			}
		}
		
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && magneticState) {
            if (showLog) Log.v(TAG, "mvalue:"+ event.values[0] + "," + event.values[1] + "," + event.values[2]);
			if(tempMagValue != null){
				if(tempMagValue[0] != event.values[0] && tempMagValue[1] != event.values[1] && tempMagValue[2] != event.values[2])tempMCount++;
				if(tempMCount >= SAMPLING_TIMES){
					magneticState = false;
					totalOK++;
					mySensorManager.unregisterListener(this, myMagneticSensor);
				}
                tempMagValue[0] = event.values[0];
                tempMagValue[1] = event.values[1];
                tempMagValue[2] = event.values[2];
			}
		}
		
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION && accelerometerLState) {
            if (showLog) Log.v(TAG, "glvalue:"+ event.values[0] + "," + event.values[1] + "," + event.values[2]);
			if(tempGLValue != null){
				if(tempGLValue[0] != event.values[0] && tempGLValue[1] != event.values[1] && tempGLValue[2] != event.values[2])tempGLCount++;
				if(tempGLCount >= SAMPLING_TIMES){
                    accelerometerLState = false;
					totalOK++;
					mySensorManager.unregisterListener(this, myGLSensor);
				}
                tempGLValue[0] = event.values[0];
                tempGLValue[1] = event.values[1];
                tempGLValue[2] = event.values[2];
			}
		}
		
		if (event.sensor.getType() == Sensor.TYPE_GRAVITY && gravityState) {
            if (showLog) Log.v(TAG, "gavalue:"+ event.values[0] + "," + event.values[1] + "," + event.values[2]);
			if(tempGaValue != null){
				if(tempGaValue[0] != event.values[0] && tempGaValue[1] != event.values[1] && tempGaValue[2] != event.values[2])tempGACount++;
				if(tempGACount >= SAMPLING_TIMES){
					gravityState = false;
					totalOK++;
					mySensorManager.unregisterListener(this, myGravitySensor);
				}
                tempGaValue[0] = event.values[0];
                tempGaValue[1] = event.values[1];
                tempGaValue[2] = event.values[2];
			}
		}

        if (showLog) Log.i(TAG, "totalOK = " + totalOK);
		if(totalOK >= 3 && !checkResult){			
			dr.Result(true);
			checkResult = true;
			stopDetect();
		}
	}
}
