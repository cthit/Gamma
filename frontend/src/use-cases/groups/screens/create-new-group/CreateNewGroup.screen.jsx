import { DigitLayout } from "@cthit/react-digit-components";
import React from "react";
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

const CreateNewGroup = ({ groupsAdd }) => (
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

export default CreateNewGroup;
