import * as yup from "yup";
import {
    USER_ACCEPTANCE_YEAR,
    USER_AGREEMENT,
    USER_CID,
    USER_EMAIL,
    USER_FIRST_NAME,
    USER_GROUPS,
    USER_ID,
    USER_LANGUAGE,
    USER_LAST_NAME,
    USER_NICK,
    USER_PASSWORD,
    USER_PHONE
} from "../../api/users/props.users.api";

export const validationSchema = text => {
    const schema = {};

    schema[USER_FIRST_NAME] = yup.string().required(text.FieldRequired);
    schema[USER_LAST_NAME] = yup.string().required(text.FieldRequired);
    schema[USER_NICK] = yup.string().required(text.FieldRequired);
    schema[USER_EMAIL] = yup.string().required(text.FieldRequired);
    schema[USER_ACCEPTANCE_YEAR] = yup.number().required(text.FieldRequired);
    schema[USER_PHONE] = yup.string().nullable();

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
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_EMAIL,
    USER_ACCEPTANCE_YEAR,
    USER_PHONE,
    USER_LANGUAGE,
    USER_GROUPS
];
