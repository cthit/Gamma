import React from "react";

import {
    DigitTranslations,
    DigitEditData,
    DigitTextField,
    DigitSelect
} from "@cthit/react-digit-components";

import translations from "./SuperGroupForm.view.translations";
import {
    NAME,
    PRETTY_NAME,
    TYPE,
    TYPE_BOARD,
    TYPE_COMMITTEE,
    TYPE_SOCIETY
} from "../../../../../api/super-groups/props.super-groups.api";
import * as yup from "yup";

function generateValidationSchema(text) {
    const schema = {};
    schema[NAME] = yup.string().required();
    schema[PRETTY_NAME] = yup.string().required();
    schema[TYPE] = yup.string().required();

    return yup.object().shape(schema);
}

function generateEditComponentData(text) {
    const componentData = {};

    componentData[NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Name,
            filled: true
        }
    };

    componentData[PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.PrettyName,
            filled: true
        }
    };

    const typeValueToTextMap = {};
    typeValueToTextMap[TYPE_SOCIETY] = text.Society;
    typeValueToTextMap[TYPE_COMMITTEE] = text.Committee;
    typeValueToTextMap[TYPE_BOARD] = text.Board;

    componentData[TYPE] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.Type,
            valueToTextMap: typeValueToTextMap,
            filled: true
        }
    };

    return componentData;
}

const SuperGroupForm = ({
    initialValues,
    onSubmit,
    titleText,
    submitText,
    backButtonTo
}) => (
    <DigitTranslations
        translations={translations}
        render={text => (
            <DigitEditData
                titleText={titleText}
                submitText={submitText}
                validationSchema={generateValidationSchema(text)}
                keysComponentData={generateEditComponentData(text)}
                initialValues={initialValues}
                onSubmit={(values, actions) => {
                    actions.setSubmitting(false);
                    onSubmit(values, actions);
                }}
                keysOrder={[NAME, PRETTY_NAME, TYPE]}
                extraButton={{
                    text: text.Cancel
                }}
                extraButtonTo={backButtonTo}
            />
        )}
    />
);

export default SuperGroupForm;
