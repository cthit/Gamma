import {
    DigitFAB,
    DigitLayout,
    DigitDesign,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import { Add } from "@material-ui/icons";
import DisplayUsersTable from "../../../../common/elements/display-users-table";

class ShowAllUsers extends Component {
    componentDidMount() {
        const { getUsersMinified, gammaLoadingFinished } = this.props;

        getUsersMinified().then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const { users } = this.props;
        return (
            <DigitIfElseRendering
                test={users != null}
                ifRender={() => (
                    <React.Fragment>
                        <DisplayUsersTable users={users} />
                        <DigitLayout.DownRightPosition>
                            <DigitDesign.Link to="/users/new">
                                <DigitFAB icon={Add} secondary />
                            </DigitDesign.Link>
                        </DigitLayout.DownRightPosition>
                    </React.Fragment>
                )}
            />
        );
    }
}

export default ShowAllUsers;
