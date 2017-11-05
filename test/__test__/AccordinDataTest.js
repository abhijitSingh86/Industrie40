import React from 'react';
import According from '../../app/assets/javascripts/components/monitoring/AccordinData';
import renderer from 'react-test-renderer';


test('Link changes the class when hovered', () => {

    var ele={"id":42,"name":"c1","opCount":3,"operationDetails":[
        [{"id":67,"label":"o1"},{"id":68,"label":"o2"},{"id":69,"label":"o3"}],
        [{"id":68,"label":"o2"},{"id":67,"label":"o1"}]]};
    const component = renderer.create(

        <According data={ele} simulationId={1} index={1}
                   activeKey={(x) =>console.log(x)}
                   onComplete={(x)=>{console.log(x)}}></According>
    );
    let tree = component.toJSON();
    expect(tree).toMatchSnapshot();

    // manually trigger the callback
    tree.props.onMouseEnter();
    // re-rendering
    tree = component.toJSON();
    expect(tree).toMatchSnapshot();

    // manually trigger the callback
    tree.props.onMouseLeave();
    // re-rendering
    tree = component.toJSON();
    expect(tree).toMatchSnapshot();
});