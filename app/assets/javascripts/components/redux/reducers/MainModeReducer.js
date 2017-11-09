import {CHANGE_MAIN_MODE , SIMULATION_ONLINE_CHECK} from '../actions'

// import { push } from 'react-router-redux';
const initialState = {
        monitor:false
    ,pagemode:"reset"

}

export default function mainModeReducer(state = initialState,action){

    // console.log("Main mode Reducer Called");
    // console.log(state);
    // console.log(action);

    switch(action.type){
        case "resetMainMode" :{
            return initialState;
        }
        case SIMULATION_ONLINE_CHECK :

            var state = Object.assign({},state,
                {
                    isLoadingComplete:action.payload.isLoadingComplete
                });
            return state;

        case "MODE":
            var state = Object.assign({},state,
                {
                    pagemode:action.payload
                });
            // push('/register');
            return state;

        case CHANGE_MAIN_MODE:
            console.log("Ch mode st");
            if(action.payload.error){
                var state = Object.assign({},state,
                    {
                        simulationId:action.payload.simulationId,
                        monitor:action.payload.monitor,
                        simulationMonitorError:action.payload.error
                        ,mode:action.payload.mode
                        ,pagemode:action.payload.pagemode
                    }    );
            }else{
                var state = Object.assign({},state,
                    {
                        simulationId:action.payload.simulationId,
                        simulationVersionId:action.payload.simulationObj.simulationVersionId,
                        monitor:action.payload.monitor,
                        simulationObj:action.payload.simulationObj
                        ,mode:action.payload.mode
                        ,pagemode:action.payload.pagemode
                        ,isLoadingComplete:false

                    });
            }
            return state;

        default :{
            return state;
        }
    }

}
