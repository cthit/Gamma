import React, { useState } from "react";
import { useHistory } from "react-router-dom";
import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitSelectFile,
    useDigitTranslations,
    useDigitToast
} from "@cthit/react-digit-components";

import translations from "./PutImage.element.translations.json";
import statusCode from "../../utils/formatters/statusCode.formatter";

const PutImage = ({ imageSrc, title, uploadFile }) => {
    const history = useHistory();
    const [text] = useDigitTranslations(translations);
    const [file, setFile] = useState(null);
    const [queueToast] = useDigitToast();

    return (
        <DigitLayout.Center>
            <DigitDesign.Card>
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle text={title} />
                </DigitDesign.CardHeader>
                <DigitDesign.CardBody>
                    <img
                        width="260px"
                        src={imageSrc}
                        alt={"Image"}
                        style={{ alignSelf: "center" }}
                    />
                    <DigitSelectFile
                        image
                        onSelectFile={setFile}
                        selectedFileName={file != null ? file.name : null}
                    />
                    <DigitLayout.Row justifyContent={"space-between"}>
                        <DigitButton
                            text={text.Back}
                            onClick={() => history.goBack()}
                            outlined
                        />
                        <DigitButton
                            primary
                            raised
                            disabled={file == null}
                            text={text.UploadImage}
                            onClick={() => {
                                uploadFile(file)
                                    .then(() => {
                                        location.reload();
                                    })
                                    .catch(error => {
                                        const code = statusCode(error);
                                        let errorMessage = text.UploadFailed;
                                        if (code === 413) {
                                            errorMessage = text.TooLargeFile;
                                        } else if (code === 415) {
                                            errorMessage = text.InvalidFileType;
                                        }
                                        queueToast({
                                            text: errorMessage,
                                            duration: 5000
                                        });
                                    });
                            }}
                        />
                    </DigitLayout.Row>
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

export default PutImage;
