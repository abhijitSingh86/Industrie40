import { combineReducers } from 'redux';
import SimulationReducer from './simulationReducer'
import MainModeReducer from './MainModeReducer'

const rootReducer = combineReducers({
    simulation: SimulationReducer,
    mainMode:MainModeReducer
});

export default rootReducer;