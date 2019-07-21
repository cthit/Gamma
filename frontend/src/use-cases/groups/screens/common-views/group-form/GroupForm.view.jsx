import {
    DigitTextField,
    DigitTranslations,
    DigitEditData,
    DigitDatePicker,
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
    FUNCTION,
    BECOMES_ACTIVE,
    BECOMES_INACTIVE,
    SUPER_GROUP
} from "../../../../../api/groups/props.groups.api";
import {
    ENGLISH_LANGUAGE,
    SWEDISH_LANGUAGE
} from "../../../../../api/utils/commonProps";
import SuperGroups from "../../../../super-groups";

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

    schema[BECOMES_ACTIVE] = yup.date().required();
    schema[BECOMES_INACTIVE] = yup.date().required();

    return yup.object().shape(schema);
}

function generateEditComponentData(text, superGroups) {
    const componentData = {};

    componentData[NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Name,
            maxLength: 50,
            filled: true
        }
    };

    componentData[PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.PrettyName,
            maxLength: 50,
            filled: true
        }
    };

    componentData[DESCRIPTION_SV] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.DescriptionSv,
            maxLength: 100,
            filled: true
        }
    };

    componentData[DESCRIPTION_EN] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.DescriptionEn,
            maxLength: 100,
            filled: true
        }
    };

    componentData[EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            maxLength: 100,
            filled: true
        }
    };

    componentData[FUNCTION_SV] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FunctionSv,
            maxLength: 100,
            filled: true
        }
    };

    componentData[FUNCTION_EN] = {
        component: DigitTextField,
        componentProps: {
            maxLength: 100,
            upperLabel: text.FunctionEn,
            filled: true
        }
    };

    componentData[BECOMES_ACTIVE] = {
        component: DigitDatePicker,
        componentProps: {
            upperLabel: text.BecomesActive,
            lowerLabel: " ",
            filled: true
        }
    };

    componentData[BECOMES_INACTIVE] = {
        component: DigitDatePicker,
        componentProps: {
            upperLabel: text.BecomesInactive,
            lowerLabel: " ",
            filled: true
        }
    };

    const superGroupMap = {};
    for (let i = 0; i < superGroups.length; i++) {
        superGroupMap[superGroups[i].id] = superGroups[i].prettyName;
    }

    componentData[SUPER_GROUP] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.SuperGroup,
            valueToTextMap: superGroupMap,
            filled: true
        }
    };

    return componentData;
}

function convertToFormFormat(input) {
    const output = {};

    output[NAME] = input[NAME];
    output[PRETTY_NAME] = input[PRETTY_NAME];
    output[EMAIL] = input[EMAIL];
    output[BECOMES_ACTIVE] = input[BECOMES_ACTIVE];
    output[BECOMES_INACTIVE] = input[BECOMES_INACTIVE];

    output[DESCRIPTION_SV] = input[DESCRIPTION][SWEDISH_LANGUAGE];
    output[DESCRIPTION_EN] = input[DESCRIPTION][ENGLISH_LANGUAGE];

    output[FUNCTION_SV] = input[FUNCTION][SWEDISH_LANGUAGE];
    output[FUNCTION_EN] = input[FUNCTION][ENGLISH_LANGUAGE];

    output[SUPER_GROUP] = input[SUPER_GROUP];

    return output;
}

function convertToCorrectFormat(input) {
    const output = {};

    output[NAME] = input[NAME];
    output[PRETTY_NAME] = input[PRETTY_NAME];
    output[EMAIL] = input[EMAIL];
    output[BECOMES_ACTIVE] = input[BECOMES_ACTIVE];
    output[BECOMES_INACTIVE] = input[BECOMES_INACTIVE];

    output[DESCRIPTION] = {};

    output[DESCRIPTION][SWEDISH_LANGUAGE] = input[DESCRIPTION_SV];
    output[DESCRIPTION][ENGLISH_LANGUAGE] = input[DESCRIPTION_EN];

    output[FUNCTION] = {};

    output[FUNCTION][SWEDISH_LANGUAGE] = input[FUNCTION_SV];
    output[FUNCTION][ENGLISH_LANGUAGE] = input[FUNCTION_EN];

    return output;
}

const GroupForm = ({ initialValues, onSubmit, superGroups }) => (
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
                initialValues={convertToFormFormat(initialValues, superGroups)}
                keysOrder={[
                    NAME,
                    PRETTY_NAME,
                    DESCRIPTION_SV,
                    DESCRIPTION_EN,
                    EMAIL,
                    FUNCTION_SV,
                    FUNCTION_EN,
                    BECOMES_ACTIVE,
                    BECOMES_INACTIVE,
                    SUPER_GROUP
                ]}
                keysComponentData={generateEditComponentData(text, superGroups)}
            />
        )}
    />
);

export default GroupForm;
