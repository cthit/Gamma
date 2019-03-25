import { DigitLayout } from "@cthit/react-digit-components";
import React, { Component } from "react";
import GroupForm from "../common-views/group-form";
import {
    BECOMES_ACTIVE,
    BECOMES_INACTIVE,
    DESCRIPTION,
    EMAIL,
    FUNCTION,
    NAME
} from "../../../../api/groups/props.groups.api";
import {
    SWEDISH_LANGUAGE,
    ENGLISH_LANGUAGE
} from "../../../../api/utils/commonProps";

function generateInitialValues() {
    const output = {};

    output[NAME] = "";
    output[EMAIL] = "";

    const description = {};
    description[SWEDISH_LANGUAGE] = "";
    description[ENGLISH_LANGUAGE] = "";
    output[DESCRIPTION] = description;

    const _function = {};
    _function[SWEDISH_LANGUAGE] = "";
    _function[ENGLISH_LANGUAGE] = "";

    output[FUNCTION] = _function;

    var today = new Date();
    var tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);

    output[BECOMES_ACTIVE] = today;
    output[BECOMES_INACTIVE] = tomorrow;

    return output;
}

class CreateNewGroup extends Component {
    componentDidMount() {
        this.props.gammaLoadingFinished();
    }

    render() {
        const { groupsAdd } = this.props;
        return (
            <DigitLayout.Center>
                <GroupForm
                    onSubmit={(values, actions) => {
                        groupsAdd(values);
                    }}
                    initialValues={generateInitialValues()}
                />
            </DigitLayout.Center>
        );
    }
}

export default CreateNewGroup;
