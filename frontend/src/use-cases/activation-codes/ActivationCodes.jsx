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
import useIsAdmin from "../../common/hooks/use-is/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";
import { NAME } from "../../api/super-groups/props.super-groups.api";

const ActivationCodes = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, [dispatch]);

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
                titleText: text.ActivationCodes,
                startOrderBy: "cid",
                search: true
            }}
            keysOrder={["id", "cid", "code", "createdAt"]}
            keysText={{
                id: text.Id,
                cid: text.Cid,
                code: text.Code,
                createdAt: text.CreatedAt
            }}
            idProp={"id"}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.WouldYouLikeToDelete + " " + data.cid
            }
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteCancel={() => text.Cancel}
            toastDeleteSuccessful={data =>
                data.cid + " " + text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.ActivationCodeDeletionFailed1 +
                " " +
                data[NAME] +
                " " +
                text.ActivationCodeDeletionFailed2
            }
            detailsTitle={data => data.cid}
            deleteButtonText={data => text.Delete + " " + data.cid}
            detailsButtonText={text.Details}
            backButtonText={text.Back}
        />
    );
};

export default ActivationCodes;
