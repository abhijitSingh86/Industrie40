import {GET_SIMULATIONS,GET_SIMULATION_WITH_ID,START_SIMULATION,STOP_SIMULATION ,
    CHANGE_COMPLETION_COUNT,GET_COMPONENT_RUNNING_STATUS,GET_ASSEMBLY_RUNNING_STATUS,
    GET_SIMULATION_RUNNING_STATUS} from '../actions'


const initialState = {
    response:""
    ,completedComponents:[]
    ,components:[]
    ,assemblies:[]
}

export default function simulationReducer(state = initialState,action){

    switch(action.type){
        case START_SIMULATION : {
            // console.log(action.payload.response === true)
            var response = action.payload.response === true ? "Started Successfully" : "Error Starting:"+action.payload.response
            return Object.assign({},state,{response:response})
        }

        case STOP_SIMULATION : {
            var response = action.payload.response === true ? "Stopped Successfully" : "Error Stopping:"+action.payload.response
            return Object.assign({},state,{response:response})
        }

        case CHANGE_COMPLETION_COUNT : {
            var st =state.completedComponents.concat([action.payload])
            return Object.assign({},state,
                {completedComponents:st})
        }

        case GET_COMPONENT_RUNNING_STATUS : {
            var cmp = action.payload.body;
            var t = [];
            state.components.map(x => {
               if(x.id != cmp.id){
                   t.push(x);
               }
            });
            t.push(action.payload.body);
            var list = state.completedComponents
            if(cmp.isComplete){
                list = list.concat([cmp]);
            }

            return Object.assign({},state,{components:t,completedComponents:list})
        }

        case GET_SIMULATION_RUNNING_STATUS : {
            var body = action.payload.body;
            var t = [];
            state.components.map(x => {
                if(x.isComplete){
                    t.push(x);
                }
            });
            return Object.assign({},state,{components:body.components,completedComponents:t , assemblies:body.assemblies})
        }

        case GET_ASSEMBLY_RUNNING_STATUS : {
            var asm = action.payload;
            var t = [];
            state.assemblies.map(x => {
                if(x.id != asm.id){
                    t.push(x);
                }
            });
            t.push(asm);


            return Object.assign({},state,{assemblies:t})
        }
        case GET_SIMULATIONS: return Object.assign({},state,{body:action.payload.data.body});

        case GET_SIMULATION_WITH_ID: return Object.assign({},state,{body:action.payload.data.body})

        default :{
            return state;
        }
    }

}
