import React from "react";
import * as yup from "yup";

import { CID } from "../../../../../api/whitelist/props.whitelist.api";

import { DigitTextField, DigitEditData } from "@cthit/react-digit-components";

function generateValidationSchema(text) {
    const schema = {};

    schema[CID] = yup.string().required(text.fieldRequiredText);

    return yup.object().shape(schema);
}

function generateEditComponentData(text) {
    const componentData = {};

    componentData[CID] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.cidInputText
        }
    };

    return componentData;
}

const WhitelistItemForm = ({
    onSubmit,
    initialValues,
    titleText,
    cidInputText,
    fieldRequiredText,
    submitText
}) => (
    <DigitEditData
        validationSchema={generateValidationSchema({
            fieldRequiredText: fieldRequiredText
        })}
        initialValues={initialValues}
        onSubmit={onSubmit}
        titleText={titleText}
        submitText={submitText}
        keysOrder={["cid"]}
        keysComponentData={generateEditComponentData({
            cidInputText: cidInputText
        })}
    />
);

export default WhitelistItemForm;
