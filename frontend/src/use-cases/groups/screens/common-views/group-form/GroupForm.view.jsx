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
    FUNCTION,
    TYPE,
    TYPE_SOCIETY,
    TYPE_COMMITTEE,
    TYPE_BOARD
} from "../../../../../api/groups/props.groups.api";
import {
    SWEDISH_LANGUAGE,
    ENGLISH_LANGUAGE
} from "../../../../../api/utils/commonProps";

function generateValidationSchema(text) {
    const descriptionSchema = {};
    descriptionSchema[SWEDISH_LANGUAGE] = yup.string().required();
    descriptionSchema[ENGLISH_LANGUAGE] = yup.string().required();

    const functionSchema = {};
    functionSchema[SWEDISH_LANGUAGE] = yup.string().required();
    functionSchema[ENGLISH_LANGUAGE] = yup.string().required();

    const schema = {};
    schema[NAME] = yup.string().required();
    schema[PRETTY_NAME] = yup.string().required();
    schema[DESCRIPTION] = yup
        .object()
        .shape(descriptionSchema)
        .required();
    schema[EMAIL] = yup.string().required();
    schema[FUNCTION] = yup
        .object()
        .shape(functionSchema)
        .required();
    schema[TYPE] = yup.string().required();

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

    componentData[DESCRIPTION + "." + SWEDISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.DescriptionSv
        }
    };

    componentData[DESCRIPTION + "." + ENGLISH_LANGUAGE] = {
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

    componentData[FUNCTION + "." + SWEDISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FunctionSv
        }
    };

    componentData[FUNCTION + "." + ENGLISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FunctionEn
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
            valueToTextMap: typeValueToTextMap
        }
    };

    return componentData;
}

const GroupForm = ({ initialValues, onSubmit }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Groups.Screen.GroupForm"
        render={text => (
            <DigitEditData
                titleText={text.Group}
                submitText={text.SaveGroup}
                validationSchema={generateValidationSchema(text)}
                onSubmit={onSubmit}
                initialValues={initialValues}
                keysOrder={[
                    NAME,
                    PRETTY_NAME,
                    DESCRIPTION + "." + SWEDISH_LANGUAGE,
                    DESCRIPTION + "." + ENGLISH_LANGUAGE,
                    EMAIL,
                    FUNCTION + "." + SWEDISH_LANGUAGE,
                    FUNCTION + "." + ENGLISH_LANGUAGE,
                    TYPE
                ]}
                keysComponentData={generateEditComponentData(text)}
            />
        )}
    />
);

export default GroupForm;
