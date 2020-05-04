import * as yup from "yup";
import { DigitTextField } from "@cthit/react-digit-components";
import {
    WHITELIST_CID,
    WHITELIST_ID
} from "../../api/whitelist/props.whitelist.api";

export const validationSchema = text => {
    const schema = {};

    schema[WHITELIST_CID] = yup
        .string()
        .min(4)
        .required(text.Cid + text.IsRequired);

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const output = {};

    output[WHITELIST_CID] = "";

    return output;
};

export const keysComponentData = () => {
    const componentData = {};
    componentData[WHITELIST_CID] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 10,
            alignSelf: "center",
            flex: "1"
        }
    };

    return componentData;
};

export const keysText = text => {
    const keysText = {};

    keysText[WHITELIST_ID] = text.Id;
    keysText[WHITELIST_CID] = text.Cid;

    return keysText;
};

export const keysOrder = () => [WHITELIST_CID];
