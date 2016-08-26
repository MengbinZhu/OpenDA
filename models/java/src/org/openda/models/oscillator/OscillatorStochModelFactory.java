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
package org.openda.models.oscillator;
import org.openda.interfaces.IStochModelInstance;
import org.openda.interfaces.IStochModelPostProcessor;
import org.openda.models.simpleModel.SimpleStochModelFactory;

import java.io.File;


/**
 * Factory for creating Stochastic Model instances.
 * Creates OscillatorSochModelInstances
 */
public class OscillatorStochModelFactory extends SimpleStochModelFactory {
	
    public IStochModelInstance getInstance(OutputLevel outputLevel) {
    	OscillatorStochModelInstance result = new OscillatorStochModelInstance(this.workingDir, this.arguments[0]);
    	result.outputLevel = outputLevel;
        return result;
    }

    public IStochModelPostProcessor getPostprocessorInstance(File instanceDir) {
        throw new UnsupportedOperationException("org.openda.models.oscillator.OscillatorStochModelFactory.getPostprocessorInstance(): Not implemented yet.");
    }

	public void finish() {
		// no action needed (yet)
	}
}
