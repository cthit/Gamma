import React, { useEffect } from "react";
import {
    DigitCRUD,
    DigitTextField,
    useDigitTranslations
} from "@cthit/react-digit-components";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { getWebsite, getWebsites } from "../../api/websites/get.websites.api";
import { editWebsite } from "../../api/websites/put.websites.api";
import { deleteWebsite } from "../../api/websites/delete.websites.api";
import { addWebsite } from "../../api/websites/post.websites.api";
import translations from "./Websites.translations";
import * as yup from "yup";

const Websites = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

    return (
        <DigitCRUD
            name={"websites"}
            path={"/websites"}
            readAllRequest={getWebsites}
            readOneRequest={getWebsite}
            updateRequest={editWebsite}
            deleteRequest={deleteWebsite}
            createRequest={addWebsite}
            tableProps={{
                titleText: text.Websites
            }}
            keysOrder={["id", "name", "prettyName"]}
            keysText={{
                id: text.Id,
                name: text.Swedish,
                prettyName: text.English
            }}
            idProp={"id"}
            formComponentData={{
                name: {
                    component: DigitTextField,
                    componentProps: {
                        outlined: true
                    }
                },
                prettyName: {
                    component: DigitTextField,
                    componentProps: {
                        outlined: true
                    }
                }
            }}
            formValidationSchema={yup.object().shape({
                name: yup.string().required(),
                prettyName: yup.string().required()
            })}
            formInitialValues={{
                name: "",
                prettyName: ""
            }}
        />
    );
};

export default Websites;
