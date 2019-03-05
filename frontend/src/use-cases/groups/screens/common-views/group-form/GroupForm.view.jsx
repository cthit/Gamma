import {
    DigitTextField,
    DigitTranslations,
    DigitEditData,
    DigitSelect
} from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";
import translations from "./GroupForm.view.translations";
import {
    NAME,
    PRETTY_NAME,
    DESCRIPTION,
    EMAIL,
    FUNCTION
} from "../../../../../api/groups/props.groups.api";
import {
    ENGLISH_LANGUAGE,
    SWEDISH_LANGUAGE
} from "../../../../../api/utils/commonProps";

const DESCRIPTION_SV = "description_sv";
const DESCRIPTION_EN = "description_en";
const FUNCTION_SV = "function_sv";
const FUNCTION_EN = "function_en";

function generateValidationSchema(text) {
    const schema = {};

    schema[NAME] = yup.string().required();
    schema[PRETTY_NAME] = yup.string().required();
    schema[EMAIL] = yup.string().required();

    schema[DESCRIPTION_SV] = yup.string().required();
    schema[DESCRIPTION_EN] = yup.string().required();

    schema[FUNCTION_SV] = yup.string().required();
    schema[FUNCTION_EN] = yup.string().required();

    return yup.object().shape(schema);
}

function generateEditComponentData(text) {
    const componentData = {};

    componentData[NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Name
        }
    };

    componentData[PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.PrettyName
        }
    };

    componentData[DESCRIPTION_SV] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.DescriptionSv
        }
    };

    componentData[DESCRIPTION_EN] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.DescriptionEn
        }
    };

    componentData[EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email
        }
    };

    componentData[FUNCTION_SV] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FunctionSv
        }
    };

    componentData[FUNCTION_EN] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FunctionEn
        }
    };

    return componentData;
}

function convertToFormFormat(input) {
    const output = {};

    output[NAME] = input[NAME];
    output[PRETTY_NAME] = input[PRETTY_NAME];
    output[EMAIL] = input[EMAIL];

    output[DESCRIPTION_SV] = input[DESCRIPTION][SWEDISH_LANGUAGE];
    output[DESCRIPTION_EN] = input[DESCRIPTION][ENGLISH_LANGUAGE];

    output[FUNCTION_SV] = input[FUNCTION][SWEDISH_LANGUAGE];
    output[FUNCTION_EN] = input[FUNCTION][ENGLISH_LANGUAGE];

    return output;
}

function convertToCorrectFormat(input) {
    const output = {};

    output[NAME] = input[NAME];
    output[PRETTY_NAME] = input[PRETTY_NAME];
    output[EMAIL] = input[EMAIL];

    output[DESCRIPTION][SWEDISH_LANGUAGE] = input[DESCRIPTION_SV];
    output[DESCRIPTION][ENGLISH_LANGUAGE] = input[DESCRIPTION_EN];

    output[FUNCTION][SWEDISH_LANGUAGE] = input[FUNCTION_SV];
    output[FUNCTION][ENGLISH_LANGUAGE] = input[FUNCTION_EN];

    return output;
}

const GroupForm = ({ initialValues, onSubmit }) => (
    <DigitTranslations
        translations={translations}
        render={text => (
            <DigitEditData
                titleText={text.Group}
                submitText={text.SaveGroup}
                validationSchema={generateValidationSchema(text)}
                onSubmit={(values, actions) => {
                    actions.setSubmitting(false);
                    onSubmit(convertToCorrectFormat(values));
                }}
                initialValues={convertToFormFormat(initialValues)}
                keysOrder={[
                    NAME,
                    PRETTY_NAME,
                    DESCRIPTION_SV,
                    DESCRIPTION_EN,
                    EMAIL,
                    FUNCTION_SV,
                    FUNCTION_EN
                ]}
                keysComponentData={generateEditComponentData(text)}
            />
        )}
    />
);

export default GroupForm;
