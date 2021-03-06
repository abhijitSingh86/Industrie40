import {GET_SIMULATIONS,GET_SIMULATION_WITH_ID,START_SIMULATION,STOP_SIMULATION ,
    CHANGE_COMPLETION_COUNT,GET_COMPONENT_RUNNING_STATUS,GET_ASSEMBLY_RUNNING_STATUS,
    GET_SIMULATION_RUNNING_STATUS} from '../actions'


const initialState = {
    response:""
    ,completedComponents:[]
    ,components:[]
    ,assemblies:[]
    ,isSimulationComplete:false
    ,isStarted:false
    ,simulationTime:{
        sttime:0,
        ettime:0
    }
}

export default function simulationReducer(state = initialState,action){

    switch(action.type){

        case "resetSimulation" :{
            return initialState;
        }
        case START_SIMULATION : {
            // console.log(action.payload.response === true)
            var response = action.payload.response === true ? "Started Successfully" : "Error Starting:"+action.payload.response
            return Object.assign({},state,{response:response , isSimulationComplete:false,isStarted:true , simulationTime:{sttime:1,ettime:0}})
        }

        case STOP_SIMULATION : {
            if(action.payload.mode != "view"){
                var response = action.payload.response.ettime !=0 ? "Stopped Successfully" : "Stopped in progress Simulation"
            }
            var state = {...state , response:response , simulationTime:action.payload.response}

            return state;
        }

        case CHANGE_COMPLETION_COUNT : {
            var st =state.completedComponents.concat([action.payload])
            return Object.assign({},state,
                {completedComponents:st})
        }

        case GET_SIMULATION_RUNNING_STATUS : {
            var body = action.payload.body;
            var t = [];
            body.components.map(x => {
                if(x.isComplete){
                    t.push(x);
                }
            });



            return Object.assign({},state,{components:body.components,completedComponents:t , assemblies:body.assemblies ,
                isSimulationComplete:body.isSimulationComplete})
        }


        case GET_SIMULATIONS: return Object.assign({},state,{body:action.payload.data.body});

        case GET_SIMULATION_WITH_ID: return Object.assign({},state,{body:action.payload.data.body})

        default :{
            return state;
        }
    }

}
