import * as yup from "yup";
import {
    USER_ACCEPTANCE_YEAR,
    USER_CID,
    USER_EMAIL,
    USER_FIRST_NAME,
    USER_GROUPS,
    USER_LANGUAGE,
    USER_LAST_NAME,
    USER_NICK,
    USER_PASSWORD,
    USER_PHONE,
    USER_RELATIONSHIPS,
    USER_AGREEMENT,
    USER_ID
} from "../../api/users/props.users.api";

export const createValidationSchema = text => {
    const schema = {};
    schema[USER_FIRST_NAME] = yup
        .string()
        .required(text.FirstName + text.IsRequired);

    schema[USER_LAST_NAME] = yup
        .string()
        .required(text.LastName + text.IsRequired);

    schema[USER_NICK] = yup.string().required(text.Nick + text.IsRequired);
    schema[USER_EMAIL] = yup.string().required(text.Email + text.IsRequired);

    schema[USER_ACCEPTANCE_YEAR] = yup
        .number()
        .required(text.AcceptanceYear + text.IsRequired);

    schema[USER_PHONE] = yup.string();

    schema[USER_AGREEMENT] = yup
        .boolean()
        .oneOf([true])
        .required(text.YouMustAccept);

    schema[USER_CID] = yup
        .string()
        .min(4, text.Min4CharCid)
        .max(12)
        .required(text.Cid + text.IsRequired);

    schema[USER_PASSWORD] = yup
        .string()
        .min(8, text.PasswordTooShort)
        .required(text.Password + text.IsRequired);

    return yup.object().shape(schema);
};

export const updateValidationSchema = text => {
    const schema = {};
    schema[USER_FIRST_NAME] = yup
        .string()
        .required(text.FirstName + text.IsRequired);

    schema[USER_LAST_NAME] = yup
        .string()
        .required(text.LastName + text.IsRequired);
    schema[USER_NICK] = yup.string().required(text.Nick + text.IsRequired);
    schema[USER_EMAIL] = yup.string().required(text.Email + text.IsRequired);

    schema[USER_ACCEPTANCE_YEAR] = yup
        .number()
        .required(text.AcceptanceYear + text.IsRequired);

    schema[USER_PHONE] = yup.string();
    return yup.object().shape(schema);
};

export const initialValues = () => {
    const output = {};

    output[USER_FIRST_NAME] = "";
    output[USER_LAST_NAME] = "";
    output[USER_NICK] = "";
    output[USER_EMAIL] = "";
    output[USER_ACCEPTANCE_YEAR] = "";
    output[USER_LANGUAGE] = "";
    output[USER_AGREEMENT] = false;
    output[USER_CID] = "";
    output[USER_PASSWORD] = "";
    output[USER_PHONE] = "";

    return output;
};

export const keysText = text => {
    const output = {};

    output[USER_ID] = text.Id;
    output[USER_CID] = text.Cid;
    output[USER_FIRST_NAME] = text.FirstName;
    output[USER_LAST_NAME] = text.LastName;
    output[USER_NICK] = text.Nick;
    output[USER_EMAIL] = text.Email;
    output[USER_ACCEPTANCE_YEAR] = text.AcceptanceYear;
    output[USER_LANGUAGE] = text.Language;
    output[USER_CID] = text.Cid;
    output[USER_AGREEMENT] = text.AcceptUserAgreement;
    output[USER_PASSWORD] = text.Password;
    output[USER_PHONE] = text.Phone;

    return output;
};

export const keysOrder = () => [
    USER_CID,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_EMAIL,
    USER_ACCEPTANCE_YEAR,
    USER_PHONE,
    USER_LANGUAGE,
    USER_AGREEMENT,
    USER_GROUPS
];

export const readOneKeysOrder = () => [
    USER_CID,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_EMAIL,
    USER_ACCEPTANCE_YEAR,
    USER_PHONE,
    USER_LANGUAGE,
    USER_RELATIONSHIPS
];

export const readAllKeysOrder = () => [
    USER_CID,
    USER_FIRST_NAME,
    USER_NICK,
    USER_LAST_NAME,
    USER_ACCEPTANCE_YEAR
];

export const updateKeysOrder = () => [
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_EMAIL,
    USER_ACCEPTANCE_YEAR,
    USER_PHONE,
    USER_LANGUAGE
];

export const createKeysOrder = () => [
    USER_CID,
    USER_PASSWORD,
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_EMAIL,
    USER_ACCEPTANCE_YEAR,
    USER_PHONE,
    USER_LANGUAGE,
    USER_AGREEMENT,
    USER_GROUPS
];
