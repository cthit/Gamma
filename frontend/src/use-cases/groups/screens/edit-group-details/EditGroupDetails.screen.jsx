import {
    DigitLayout,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import GroupForm from "../common-views/group-form";

class EditGroupDetails extends Component {
    componentDidMount() {
        const { groupId, getGroup, gammaLoadingFinished } = this.props;

        getGroup(groupId).then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const { group, groupsChange, groupId } = this.props;
        return (
            <DigitIfElseRendering
                test={group != null}
                ifRender={() => (
                    <DigitLayout.Center>
                        <GroupForm
                            onSubmit={(values, actions) => {
                                groupsChange(values, groupId);
                            }}
                            initialValues={group}
                        />
                    </DigitLayout.Center>
                )}
            />
        );
    }
}

export default EditGroupDetails;
