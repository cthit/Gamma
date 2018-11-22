import React from "react";
import * as yup from "yup";
import EditWebsites from "../../../../../common/views/edit-websites";
import translations from "./UserForm.view.translations.json";

import {
    FIRST_NAME,
    LAST_NAME,
    NICKNAME,
    EMAIL,
    ACCEPTANCE_YEAR,
    WEBSITES
} from "../../../../../api/users/props.users.api";

import {
    DigitTranslations,
    DigitTextField,
    DigitEditData,
    DigitSelect,
    DigitDesign
} from "@cthit/react-digit-components";

function _getCurrentYear() {
    return new Date().getFullYear() + "";
}

function _generateAcceptanceYears() {
    const output = {};
    const startYear = 2001;
    const currentYear = _getCurrentYear();
    for (var i = currentYear; i >= startYear; i--) {
        output[i] = i + "";
    }

    return output;
}

function generateValidationSchema(text) {
    const schema = {};
    schema[FIRST_NAME] = yup.string().required(text.FieldRequired);
    schema[LAST_NAME] = yup.string().required(text.FieldRequired);
    schema[NICKNAME] = yup.string().required(text.FieldRequired);
    schema[EMAIL] = yup.string().required(text.FieldRequired);
    schema[ACCEPTANCE_YEAR] = yup.string().required(text.FieldRequired);
    schema[WEBSITES] = yup.array().of(yup.object());

    return yup.object().shape(schema);
}

function generateEditComponentData(text, availableWebsites) {
    const componentData = {};

    componentData[FIRST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FirstName
        }
    };
    componentData[LAST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.LastName
        }
    };
    componentData[NICKNAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Nick
        }
    };
    componentData[EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email
        }
    };
    componentData[ACCEPTANCE_YEAR] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.AcceptanceYear,
            valueToTextMap: _generateAcceptanceYears(),
            reverse: true
        }
    };
    componentData[WEBSITES] = {
        array: true,
        component: EditWebsites,
        componentProps: {
            availableWebsites: availableWebsites
        }
    };

    return componentData;
}

const UserForm = ({
    initialValues,
    onSubmit,
    titleText,
    submitText,
    availableWebsites
}) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Users.Screen.CommonViews.UserForm"
        render={text => (
            <DigitEditData
                titleText={titleText}
                submitText={submitText}
                initialValues={initialValues}
                onSubmit={(values, actions) => {
                    onSubmit(values, actions);
                }}
                validationSchema={generateValidationSchema(text)}
                keysOrder={[
                    FIRST_NAME,
                    LAST_NAME,
                    NICKNAME,
                    EMAIL,
                    ACCEPTANCE_YEAR,
                    WEBSITES
                ]}
                keysComponentData={generateEditComponentData(
                    text,
                    availableWebsites
                )}
            />
        )}
    />
);

export default UserForm;
