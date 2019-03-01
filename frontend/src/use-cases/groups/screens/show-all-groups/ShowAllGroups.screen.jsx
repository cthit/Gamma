import React, { Component } from "react";
import {
    DigitFAB,
    DigitLayout,
    DigitDesign
} from "@cthit/react-digit-components";
import { Add } from "@material-ui/icons";
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
        return (
            <React.Fragment>
                <DisplayGroupsTable groups={groups} />;
                <DigitLayout.DownRightPosition>
                    <DigitDesign.Link to="/groups/new">
                        <DigitFAB icon={Add} secondary />
                    </DigitDesign.Link>
                </DigitLayout.DownRightPosition>
            </React.Fragment>
        );
    }
}

export default ShowAllGroups;
