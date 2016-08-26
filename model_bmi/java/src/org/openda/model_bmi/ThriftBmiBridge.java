/* OpenDA v2.3.1 
* Copyright (c) 2016 OpenDA Association 
* All rights reserved.
* 
* This file is part of OpenDA. 
* 
* OpenDA is free software: you can redistribute it and/or modify 
* it under the terms of the GNU Lesser General Public License as 
* published by the Free Software Foundation, either version 3 of 
* the License, or (at your option) any later version. 
* 
* OpenDA is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
* GNU Lesser General Public License for more details. 
* 
* You should have received a copy of the GNU Lesser General Public License
* along with OpenDA.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.openda.model_bmi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import bmi.BMIGridType;
import bmi.BMIModelException;
import bmi.EBMI;

import org.openda.model_bmi.thrift.BMIService;
import org.openda.model_bmi.thrift.BMIService.Client;
import org.openda.model_bmi.thrift.ModelException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;

/**
 * Implementation of the Java BMI Interface that communicates with a remote BMI
 * model using Thrift.
 * 
 * Adapter between BMI (from the BMI Java binding) and BMIService.Client
 * (generated by Thrift).
 *
 * Note: The bmi.thrift file is used by Thrift to generate Java classes that
 * adhere to the BMI interface. However the Java classes generated by Thrift use
 * different input/output types than the BMI interface (e.g. ArrayLists whereas
 * the BMI interface requires arrays) and throw different exceptions (e.g.
 * TException whereas the BMI interface requires BMIModelException). Therefore
 * the generated Java classes cannot directly implement the BMI interface. For
 * this reason need this adapter that does implement the BMI interface and
 * delegates everything to the Java class generated by Thrift.
 * 
 * @author Niels Drost
 *
 */
public class ThriftBmiBridge implements EBMI {

	private final Client client;
	private final Process process;
	private final TTransport transport;

	/**
	 * It is up to the user to create the model (possibly a remote process), and
	 * a connection to it.
	 * 
	 * @param client
	 */
	public ThriftBmiBridge(BMIService.Client client, Process process, TTransport transport) {
		this.client = client;
		this.process = process;
		this.transport = transport;
	}

	@Override
	public void initialize(String file) throws BMIModelException {
		try {
			client.initialize(file);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}
	}

	@Override
	public void update() throws BMIModelException {
		try {
			client.update();
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}
	}

	@Override
	public void updateUntil(double time) throws BMIModelException {
		try {
			client.update_until(time);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}
	}

	@Override
	public void finalizeModel() throws BMIModelException {
		try {
			client.finalize_model();
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		} finally {
			finish();
		}
	}

	@Override
	public String getComponentName() throws BMIModelException {
		try {
			return client.get_component_name();
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}
	}

	@Override
	public String[] getInputVarNames() throws BMIModelException {
		try {
			List<String> result = client.get_input_var_names();
			return result.toArray(new String[result.size()]);
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model", e);
		}
	}

	@Override
	public String[] getOutputVarNames() throws BMIModelException {
		try {
			List<String> result = client.get_output_var_names();
			return result.toArray(new String[result.size()]);
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public String getVarType(String longVarName) throws BMIModelException {
		try {
			return client.get_var_type(longVarName);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public String getVarUnits(String longVarName) throws BMIModelException {
		try {
			return client.get_var_units(longVarName);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public int getVarRank(String longVarName) throws BMIModelException {
		try {
			return client.get_var_rank(longVarName);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double getStartTime() throws BMIModelException {
		try {
			return client.get_start_time();
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double getEndTime() throws BMIModelException {
		try {
			return client.get_end_time();
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double getCurrentTime() throws BMIModelException {
		try {
			return client.get_current_time();
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	// get a 1d array of doubles from the buffer given
	private static double[] bufferToDoubleArray(ByteBuffer buffer) {
		buffer.order(ByteOrder.nativeOrder());
		DoubleBuffer doubles = buffer.asDoubleBuffer();

		if (doubles.hasArray()) {
			return doubles.array();
		} else {
			double[] resultArray = new double[doubles.capacity()];
			doubles.get(resultArray);
			return resultArray;
		}
	}

	// get a 1d array of floats from the buffer given
	private static float[] bufferToFloatArray(ByteBuffer buffer) {
		buffer.order(ByteOrder.nativeOrder());
		FloatBuffer floats = buffer.asFloatBuffer();

		if (floats.hasArray()) {
			return floats.array();
		} else {
			float[] resultArray = new float[floats.capacity()];
			floats.get(resultArray);
			return resultArray;
		}
	}

	private static List<Integer> intArrayToList(int[] array) {
		List<Integer> result = new ArrayList<Integer>();

		for (int integer : array) {
			result.add(integer);
		}

		return result;
	}

	private static int[] listToIntArray(List<Integer> list) {
		int[] result = new int[list.size()];

		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}

		return result;
	}

	private static double[] listToDoubleArray(List<Double> list) {
		double[] result = new double[list.size()];

		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}

		return result;
	}

	@Override
	public double[] getDouble(String longVarName) throws BMIModelException {
		try {
			ByteBuffer result = client.get_value(longVarName);
			return bufferToDoubleArray(result);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double[] getDoubleAtIndices(String longVarName, int[] indices) throws BMIModelException {
		try {
			List<Integer> indicesList = intArrayToList(indices);

			ByteBuffer result = client.get_value_at_indices(longVarName, indicesList);
			return bufferToDoubleArray(result);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public void setDouble(String longVarName, double[] src) throws BMIModelException {
		try {
			// add data to a buffer
			ByteBuffer buffer = ByteBuffer.allocate(src.length * 8);
			buffer.order(ByteOrder.nativeOrder());
			buffer.asDoubleBuffer().put(src);

			client.set_value(longVarName, buffer);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public void setDoubleAtIndices(String longVarName, int[] indices, double[] src) throws BMIModelException {
		try {
			// add data to a buffer
			ByteBuffer buffer = ByteBuffer.allocate(src.length * 8);
			buffer.order(ByteOrder.nativeOrder());
			buffer.asDoubleBuffer().put(src);

			List<Integer> indicesList = intArrayToList(indices);

			client.set_value_at_indices(longVarName, indicesList, buffer);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public float[] getFloat(String LongVarName) throws BMIModelException {
		try {
			ByteBuffer result = client.get_value(LongVarName);
			return bufferToFloatArray(result);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public float[] getFloatAtIndices(String LongVarName, int[] indices) throws BMIModelException {
		try {
			List<Integer> indicesList = intArrayToList(indices);

			ByteBuffer result = client.get_value_at_indices(LongVarName, indicesList);
			return bufferToFloatArray(result);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public void setFloat(String LongVarName, float[] src) throws BMIModelException {
		try {
			// add data to a buffer
			ByteBuffer buffer = ByteBuffer.allocate(src.length * 4);
			buffer.order(ByteOrder.nativeOrder());
			buffer.asFloatBuffer().put(src);

			client.set_value(LongVarName, buffer);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public void setFloatAtIndices(String LongVarName, int[] indices, float[] src) throws BMIModelException {
		try {
			// add data to a buffer
			ByteBuffer buffer = ByteBuffer.allocate(src.length * 4);
			buffer.order(ByteOrder.nativeOrder());
			buffer.asFloatBuffer().put(src);

			List<Integer> indicesList = intArrayToList(indices);

			client.set_value_at_indices(LongVarName, indicesList, buffer);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public BMIGridType getGridType(String LongVarName) throws BMIModelException {
		try {
			// convert from Thrift type to binding type
			return BMIGridType.findByValue(client.get_grid_type(LongVarName).getValue());
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public int[] getGridShape(String LongVarName) throws BMIModelException {
		try {
			// convert from Thrift type to binding type
			return listToIntArray(client.get_grid_shape(LongVarName));
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double[] getGridSpacing(String LongVarName) throws BMIModelException {
		try {
			// convert from Thrift type to binding type
			return listToDoubleArray(client.get_grid_spacing(LongVarName));
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double[] getGridOrigin(String LongVarName) throws BMIModelException {
		try {
			// convert from Thrift type to binding type
			return listToDoubleArray(client.get_grid_origin(LongVarName));
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	private void finish() throws BMIModelException {
		this.transport.close();

		this.process.destroy();

		try {
			this.process.waitFor();
		} catch (InterruptedException e) {
			throw new BMIModelException(e);
		}
	}

	@Override
	public void updateFrac(double timeFrac) throws BMIModelException {
		try {
			client.update_frac(timeFrac);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	

	@Override
	public int getVarSize(String longVarName) throws BMIModelException {
		try {
			return client.get_var_size(longVarName);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public int getVarNbytes(String longVarName) throws BMIModelException {
		try {
			return client.get_var_nbytes(longVarName);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double getTimeStep() throws BMIModelException {
		try {
			return client.get_time_step();
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public String getTimeUnits() throws BMIModelException {
		try {
			return client.get_time_units();
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double[] getGridX(String longVarName) throws BMIModelException {
		try {
			return listToDoubleArray(client.get_grid_x(longVarName));
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double[] getGridY(String longVarName) throws BMIModelException {
		try {
			return listToDoubleArray(client.get_grid_y(longVarName));
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public double[] getGridZ(String longVarName) throws BMIModelException {
		try {
			return listToDoubleArray(client.get_grid_z(longVarName));
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public int[] getGridConnectivity(String longVarName) throws BMIModelException {
		try {
			return listToIntArray(client.get_grid_connectivity(longVarName));
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public int[] getGridOffset(String longVarName) throws BMIModelException {
		try {
			return listToIntArray(client.get_grid_offset(longVarName));
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	// BMI Extended Functions

	@Override
	public void initializeConfig(String configFile) throws BMIModelException {
		try {
			client.initialize_config(configFile);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public void initializeModel() throws BMIModelException {
		try {
			client.initialize_model();
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public void setStartTime(double startTime) throws BMIModelException {
		try {
			client.set_start_time(startTime);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public void setEndTime(double endTime) throws BMIModelException {
		try {
			client.set_end_time(endTime);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public String[] getAttributeNames() throws BMIModelException {
		try {
			List<String> result = client.get_attribute_names();
			return result.toArray(new String[result.size()]);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}

	@Override
	public String getAttributeValue(String attributeName) throws BMIModelException {
		try {
			return client.get_attribute_value(attributeName);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}
	}

	@Override
	public void setAttributeValue(String attributeName, String attributeValue) throws BMIModelException {
		try {
			client.set_attribute_value(attributeName, attributeValue);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}

	}
	
	@Override
	public void saveState(String destinationFolder) throws BMIModelException {
		try {
			client.save_state(destinationFolder);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}
	}

	@Override
	public void loadState(String sourceFolder) throws BMIModelException {
		try {
			client.load_state(sourceFolder);
		} catch (ModelException e) {
			throw new BMIModelException("model function resulted in exception: " + e.getMessage());
		} catch (TException e) {
			throw new BMIModelException("failed to execute function on remote model: " + e.getMessage(), e);
		}
	}

	
}
