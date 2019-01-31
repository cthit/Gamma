import React from "react";

class ShowAllSuperGroup extends React.Component {
    componentDidMount() {
        this.props.gammaLoadingFinished();
    }

    render() {
        return <div>hej</div>;
    }
}

export default ShowAllSuperGroup;
