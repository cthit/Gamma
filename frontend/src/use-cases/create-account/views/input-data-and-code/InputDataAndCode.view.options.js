import * as yup from "yup";

import {
    DigitSelect,
    DigitSwitch,
    DigitTextField
} from "@cthit/react-digit-components";

import {
    USER_ACCEPTANCE_YEAR,
    USER_CID,
    USER_EMAIL,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_PASSWORD,
    USER_AGREEMENT
} from "api/users/props.users.api";

const CODE = "code";
const PASSWORD_CONFIRMATION = "passwordConfirmation";

const _getCurrentYear = () => {
    return new Date().getFullYear() + "";
};

const _generateAcceptanceYears = () => {
    const output = {};
    const startYear = 2001;
    const currentYear = _getCurrentYear();
    for (var i = currentYear; i >= startYear; i--) {
        output[i] = i + "";
    }
    return output;
};

export const validationSchema = text => {
    const schema = {};
    schema[USER_CID] = yup.string().required(text.Cid + text.IsRequired);
    schema[CODE] = yup.string().required(text.Code + text.IsRequired);
    schema[USER_NICK] = yup.string().required(text.Nick + text.IsRequired);
    schema[USER_FIRST_NAME] = yup
        .string()
        .required(text.FirstName + text.IsRequired);

    schema[USER_LAST_NAME] = yup
        .string()
        .required(text.LastName + text.IsRequired);
    schema[USER_EMAIL] = yup
        .string()
        .required(text.Email + text.IsRequired)
        .email(text.NotEmail)
        .matches(/(^((?!@student.chalmers.se).)*$)/, text.NonStudentEmailError);

    schema[USER_ACCEPTANCE_YEAR] = yup
        .number()
        .min(2001)
        .max(_getCurrentYear())
        .required(text.AcceptanceYear + text.IsRequired);

    schema[USER_PASSWORD] = yup
        .string()
        .min(8, text.MinimumLength)
        .required(text.Password + text.IsRequired);

    schema[PASSWORD_CONFIRMATION] = yup
        .string()
        .oneOf([yup.ref("password")], text.PasswordsDoNotMatch)
        .required(text.Password + text.IsRequired);

    schema[USER_AGREEMENT] = yup
        .boolean()
        .oneOf([true])
        .required(text.YouMustAccept);

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const initialValues = {};
    initialValues[USER_CID] = "";
    initialValues[CODE] = "";
    initialValues[USER_NICK] = "";
    initialValues[USER_FIRST_NAME] = "";
    initialValues[USER_LAST_NAME] = "";
    initialValues[USER_EMAIL] = "";
    initialValues[USER_ACCEPTANCE_YEAR] = "";
    initialValues[USER_PASSWORD] = "";
    initialValues[PASSWORD_CONFIRMATION] = "";
    initialValues[USER_AGREEMENT] = false;

    return initialValues;
};

export const keysComponentData = text => {
    const keysComponentData = {};
    keysComponentData[USER_CID] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.YourCid,
            outlined: true,
            maxLength: 10,
            size: { width: "280px" }
        }
    };

    keysComponentData[CODE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.CodeFromYourStudentEmail,
            outlined: true,
            maxLength: 15,
            size: { width: "280px" }
        }
    };

    keysComponentData[USER_NICK] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Nick,
            outlined: true,
            maxLength: 20,
            size: { width: "280px" }
        }
    };

    keysComponentData[USER_FIRST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FirstName,
            outlined: true,
            maxLength: 15,
            size: { width: "280px" }
        }
    };

    keysComponentData[USER_LAST_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.LastName,
            outlined: true,
            maxLength: 15,
            size: { width: "280px" }
        }
    };

    keysComponentData[USER_EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.NonStudentEmail,
            outlined: true,
            maxLength: 100,
            size: { width: "280px" }
        }
    };

    keysComponentData[USER_ACCEPTANCE_YEAR] = {
        component: DigitSelect,
        componentProps: {
            valueToTextMap: _generateAcceptanceYears(),
            upperLabel: text.WhichYearDidYouStart,
            reverse: true,
            outlined: true,
            size: { width: "280px" }
        }
    };

    keysComponentData[USER_PASSWORD] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Password,
            outlined: true,
            password: true,
            size: { width: "280px" }
        }
    };

    keysComponentData[PASSWORD_CONFIRMATION] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.ConfirmPassword,
            outlined: true,
            password: true,
            size: { width: "280px" }
        }
    };

    keysComponentData[USER_AGREEMENT] = {
        component: DigitSwitch,
        componentProps: {
            label: text.AcceptUserAgreement,
            primary: true,
            size: { width: "280px" }
        }
    };

    return keysComponentData;
};

export const keysOrder = () => [
    USER_CID,
    CODE,
    USER_PASSWORD,
    PASSWORD_CONFIRMATION,
    USER_NICK,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_EMAIL,
    USER_ACCEPTANCE_YEAR,
    USER_AGREEMENT
];
