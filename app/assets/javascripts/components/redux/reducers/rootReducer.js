import { combineReducers } from 'redux';
import SimulationReducer from './simulationReducer';
import MainModeReducer from './MainModeReducer';
import RegistrationReducer from './RegistrationReducer';``

const rootReducer = combineReducers({
    simulation: SimulationReducer,
    mainMode:MainModeReducer,
    registration:RegistrationReducer
});

export default rootReducer;