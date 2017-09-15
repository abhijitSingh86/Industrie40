import {CHANGE_MAIN_MODE} from '../actions'


const initialState = {
        monitor:false
}

export default function mainModeReducer(state = initialState,action){

    // console.log("Main mode Reducer Called");
    // console.log(state);
    // console.log(action);

    switch(action.type){
        case CHANGE_MAIN_MODE:
            console.log("Ch mode st");
            if(action.payload.error){
                var state = Object.assign({},state,
                    {
                        simulationId:action.payload.simulationId,
                        monitor:action.payload.monitor,
                        simulationMonitorError:action.payload.error
                    }    );
            }else{
                var state = Object.assign({},state,
                    {
                        simulationId:action.payload.simulationId,
                        monitor:action.payload.monitor,
                        simulationObj:action.payload.simulationObj
                    });
            }
            return state;

        default :{
            return state;
        }
    }

}
