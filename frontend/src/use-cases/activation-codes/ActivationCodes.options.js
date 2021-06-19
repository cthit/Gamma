import {
    AC_CID,
    AC_CODE,
    AC_CREATED_AT,
    AC_ID
} from "api/activation-codes/props.activationCodes.api";

export const keysText = text => {
    const keysText = {};
    keysText[AC_ID] = text.Id;
    keysText[AC_CID] = text.Cid;
    keysText[AC_CODE] = text.Code;
    keysText[AC_CREATED_AT] = text.CreatedAt;

    return keysText;
};

export const keysOrder = () => [AC_CID, AC_CODE, AC_CREATED_AT];
