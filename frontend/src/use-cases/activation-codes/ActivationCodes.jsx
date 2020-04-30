import React from "react";
import {
    getActivationCode,
    getActivationCodes
} from "../../api/activation-codes/get.activationCodes.api";
import { deleteActivationCode } from "../../api/activation-codes/delete.activationCodes.api";
import { useDigitTranslations, DigitCRUD } from "@cthit/react-digit-components";
import translations from "./ActivationCodes.translations";
import InsufficientAccess from "../../common/views/insufficient-access";
import { NAME } from "../../api/super-groups/props.super-groups.api";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import { on401 } from "../../common/utils/error-handling/error-handling";
import FourOFour from "../four-o-four";
import FiveZeroZero from "../../app/elements/five-zero-zero";

const ActivationCodes = () => {
    const [text] = useDigitTranslations(translations);

    const admin = useGammaIsAdmin();
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
                search: true,
                flex: "1",
                startOrderByDirection: "asc"
            }}
            keysOrder={["cid", "code", "createdAt"]}
            readAllKeysOrder={["cid", "code", "createdAt"]}
            keysText={{
                id: text.Id,
                cid: text.Cid,
                code: text.Code,
                createdAt: text.CreatedAt
            }}
            idProp={"cid"}
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
            dateAndTimeProps={["createdAt"]}
            on401={on401}
            render404={() => <FourOFour />}
            render500={(error, reset) => (
                <FiveZeroZero error={error} reset={reset} />
            )}
        />
    );
};

export default ActivationCodes;
