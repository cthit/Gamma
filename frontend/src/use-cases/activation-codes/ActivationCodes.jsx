import React from "react";
import {
    getActivationCode,
    getActivationCodes
} from "../../api/activation-codes/get.activationCodes.api";
import { deleteActivationCode } from "../../api/activation-codes/delete.activationCodes.api";
import { useDigitTranslations, DigitCRUD } from "@cthit/react-digit-components";
import translations from "./ActivationCodes.translations";
import InsufficientAccess from "../../common/views/insufficient-access";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import { on401 } from "../../common/utils/error-handling/error-handling";
import FourOFour from "../four-o-four";
import FiveZeroZero from "../../app/elements/five-zero-zero";
import { keysOrder, keysText } from "./ActivationCodes.options";
import {
    AC_CID,
    AC_NAME
} from "../../api/activation-codes/props.activationCodes.api";

const ActivationCodes = () => {
    const [text] = useDigitTranslations(translations);

    const admin = useGammaIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            keysOrder={keysOrder()}
            name={"activationCodes"}
            path={"/activation-codes"}
            readAllRequest={getActivationCodes}
            readOneRequest={getActivationCode}
            deleteRequest={deleteActivationCode}
            tableProps={{
                titleText: text.ActivationCodes,
                startOrderBy: AC_CID,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px"
            }}
            keysText={keysText(text)}
            idProp={AC_CID}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.WouldYouLikeToDelete + " " + data[AC_CID]
            }
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteCancel={() => text.Cancel}
            toastDeleteSuccessful={data =>
                data[AC_CID] + " " + text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.ActivationCodeDeletionFailed1 +
                " " +
                data[AC_NAME] +
                " " +
                text.ActivationCodeDeletionFailed2
            }
            detailsTitle={data => data[AC_CID]}
            deleteButtonText={data => text.Delete + " " + data[AC_CID]}
            detailsButtonText={text.Details}
            backButtonText={text.Back}
            dateAndTimeProps={["createdAt"]}
            statusHandlers={{
                401: on401
            }}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
        />
    );
};

export default ActivationCodes;
