import { combineReducers } from 'redux';
import SimulationReducer from './simulationReducer';
import MainModeReducer from './MainModeReducer';
import RegistrationReducer from './RegistrationReducer';
import StartPageReducer from './startpagereducer'



const rootReducer = combineReducers({
    simulation: SimulationReducer,
    mainMode:MainModeReducer,
    registration:RegistrationReducer,
    startPage:StartPageReducer

});

export default rootReducer;