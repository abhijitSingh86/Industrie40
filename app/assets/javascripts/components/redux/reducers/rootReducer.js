import { combineReducers } from 'redux';
import SimulationReducer from './simulationReducer';
import MainModeReducer from './MainModeReducer';
import RegistrationReducer from './RegistrationReducer';
import StartPageReducer from './startpagereducer'

import {  routerReducer } from 'react-router-redux'

const rootReducer = combineReducers({
    simulation: SimulationReducer,
    mainMode:MainModeReducer,
    registration:RegistrationReducer,
    startPage:StartPageReducer
    ,routing: routerReducer
});

export default rootReducer;