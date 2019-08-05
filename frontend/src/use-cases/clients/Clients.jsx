import {
    DigitCRUD,
    useDigitTranslations,
    DigitTextField,
    DigitTextArea,
    DigitDialogActions,
    DigitText,
    DigitButton
} from "@cthit/react-digit-components";
import React, { useEffect } from "react";
import { getClient, getClients } from "../../api/clients/get.clients.api";
import { addClient } from "../../api/clients/post.clients.api";
import translations from "./Clients.translations";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import * as yup from "yup";
import { deleteClient } from "../../api/clients/delete.clients.api";

const Clients = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

    return (
        <DigitCRUD
            name={"clients"}
            path={"/clients"}
            readAllRequest={getClients}
            readOneRequest={getClient}
            deleteRequest={deleteClient}
            createRequest={client =>
                new Promise((resolve, reject) =>
                    addClient({
                        name: client.name,
                        description: {
                            sv: client.descriptionSv,
                            en: client.descriptionEn
                        },
                        webServerRedirectUri: client.webServerRedirectUri
                    })
                        .then(response => {
                            dispatch(
                                DigitDialogActions.digitDialogCustomOpen({
                                    title: text.YourClientSecret,
                                    onConfirm: () => {},
                                    renderMain: () => (
                                        <DigitText.Text
                                            text={response.data.clientSecret}
                                        />
                                    ),
                                    renderButtons: confirm => (
                                        <DigitButton
                                            text={text.CloseDialog}
                                            onClick={confirm}
                                        />
                                    )
                                })
                            );
                            resolve(response);
                        })
                        .catch(error => reject(error))
                )
            }
            tableProps={{
                titleText: text.Clients
            }}
            keysOrder={[
                "id",
                "name",
                "webServerRedirectUri",
                "descriptionSv",
                "descriptionEn"
            ]}
            keysText={{
                id: text.Id,
                name: text.Name,
                webServerRedirectUri: text.RedirectURI,
                descriptionSv: text.DescriptionSv,
                descriptionEn: text.DescriptionEn
            }}
            idProp={"id"}
            formValidationSchema={yup.object().shape({
                webServerRedirectUri: yup.string().required(),
                name: yup.string().required(),
                descriptionSv: yup.string().required(),
                descriptionEn: yup.string().required()
            })}
            formInitialValues={{
                webServerRedirectUri: "",
                name: "",
                descriptionSv: "",
                descriptionEn: ""
            }}
            formComponentData={{
                webServerRedirectUri: {
                    component: DigitTextField,
                    componentProps: {
                        outlined: true
                    }
                },
                name: {
                    component: DigitTextField,
                    componentProps: {
                        outlined: true
                    }
                },
                descriptionSv: {
                    component: DigitTextArea,
                    componentProps: {
                        outlined: true,
                        rows: 3
                    }
                },
                descriptionEn: {
                    component: DigitTextArea,
                    componentProps: {
                        outlined: true,
                        rows: 3
                    }
                }
            }}
        />
    );
};

export default Clients;
