import React, { Component } from "react";
import DisplayGroupsTable from "../../../../common/elements/display-groups-table/DisplayGroupsTable.element";

class ShowAllGroups extends Component {
    componentDidMount() {
        const { getGroupsMinified, gammaLoadingFinished } = this.props;

        getGroupsMinified().then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        let { groups } = this.props;
        return <DisplayGroupsTable groups={groups} />;
    }
}

export default ShowAllGroups;
