import { DigitLayout } from "@cthit/react-digit-components";
import React, { Component } from "react";
import GroupForm from "../common-views/group-form";
import {
    DESCRIPTION,
    EMAIL,
    FUNCTION,
    NAME
} from "../../../../api/groups/props.groups.api";
import {
    SWEDISH_LANGUAGE,
    ENGLISH_LANGUAGE
} from "../../../../api/utils/commonProps";
import * as PropTypes from "prop-types";

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

    return output;
}

class CreateNewGroup extends Component {
    constructor(props) {
        super(props);

        props.gammaLoadingFinished();
    }

    render() {
        const { groupsAdd } = this.props;
        return (
            <DigitLayout.Fill>
                <DigitLayout.Center>
                    <GroupForm
                        onSubmit={(values, actions) => {
                            groupsAdd(values);
                        }}
                        initialValues={generateInitialValues()}
                    />
                </DigitLayout.Center>
            </DigitLayout.Fill>
        );
    }
}

export default CreateNewGroup;
