import React, { useEffect } from "react";
import {
    DigitCRUD,
    DigitTextField,
    useDigitTranslations
} from "@cthit/react-digit-components";
import {
    getWhitelist,
    getWhitelistItem
} from "../../api/whitelist/get.whitelist.api";
import { addUsersToWhitelist } from "../../api/whitelist/post.whitelist.api";
import { editWhitelistItem } from "../../api/whitelist/put.whitelist.api";
import translations from "./Whitelist.translations";
import * as yup from "yup";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { useDispatch } from "react-redux";
import { deleteWhitelistItem } from "../../api/whitelist/delete.whitelist.api";

const Whitelist = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

    return (
        <DigitCRUD
            name={"whitelist"}
            path={"/whitelist"}
            readAllRequest={getWhitelist}
            readOneRequest={getWhitelistItem}
            createRequest={data => addUsersToWhitelist({ cids: [data.cid] })}
            updateRequest={editWhitelistItem}
            deleteRequest={deleteWhitelistItem}
            keysText={{ id: text.Id, cid: text.Cid }}
            keysOrder={["id", "cid"]}
            formComponentData={{
                cid: {
                    component: DigitTextField,
                    componentProps: {
                        outlined: true
                    }
                }
            }}
            formValidationSchema={yup.object().shape({
                cid: yup.string().required()
            })}
            formInitialValues={{
                cid: ""
            }}
            idProp={"id"}
            tableProps={{
                titleText: text.Whitelist,
                search: true
            }}
        />
    );
};

export default Whitelist;
