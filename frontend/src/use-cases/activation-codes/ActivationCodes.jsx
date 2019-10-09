import React, { useEffect } from "react";
import {
    getActivationCode,
    getActivationCodes
} from "../../api/activation-codes/get.activationCodes.api";
import { deleteActivationCode } from "../../api/activation-codes/delete.activationCodes.api";
import { useDigitTranslations, DigitCRUD } from "@cthit/react-digit-components";
import translations from "./ActivationCodes.translations";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";

const ActivationCodes = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

    const admin = useIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            name={"activationCodes"}
            path={"/activation-codes"}
            readAllRequest={getActivationCodes}
            readOneRequest={getActivationCode}
            deleteRequest={deleteActivationCode}
            tableProps={{
                titleText: text.ActivationCodes
            }}
            keysOrder={["id", "cid", "code", "createdAt"]}
            keysText={{
                id: text.Id,
                cid: text.Cid,
                code: text.Code,
                createdAt: text.CreatedAt
            }}
            idProp={"id"}
        />
    );
};

export default ActivationCodes;
