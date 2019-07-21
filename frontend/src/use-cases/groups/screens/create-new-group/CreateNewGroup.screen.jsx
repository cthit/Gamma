import {
    DigitLayout,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import GroupForm from "../common-views/group-form";
import {
    BECOMES_ACTIVE,
    BECOMES_INACTIVE,
    DESCRIPTION,
    EMAIL,
    FUNCTION,
    NAME,
    PRETTY_NAME,
    SUPER_GROUP
} from "../../../../api/groups/props.groups.api";
import {
    SWEDISH_LANGUAGE,
    ENGLISH_LANGUAGE
} from "../../../../api/utils/commonProps";

function generateInitialValues() {
    const output = {};

    output[NAME] = "";
    output[PRETTY_NAME] = "";
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

    output[SUPER_GROUP] = "";

    return output;
}

class CreateNewGroup extends Component {
    componentDidMount() {
        const { gammaLoadingFinished, getSuperGroups } = this.props;
        getSuperGroups().then(() => gammaLoadingFinished());
        this.props.gammaLoadingFinished();
    }

    render() {
        const { groupsAdd, superGroups } = this.props;
        return (
            <DigitIfElseRendering
                test={superGroups != null}
                ifRender={() => (
                    <DigitLayout.Center>
                        <GroupForm
                            onSubmit={(values, actions) => {
                                groupsAdd(values);
                            }}
                            initialValues={generateInitialValues()}
                            superGroups={superGroups}
                        />
                    </DigitLayout.Center>
                )}
            />
        );
    }
}

export default CreateNewGroup;
