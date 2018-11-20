import React from "react";

class Home extends React.Component {
    constructor(props) {
        super();

        props.gammaLoadingFinished();
    }

    render() {
        return <div>Hej</div>;
    }
}

export default Home;
