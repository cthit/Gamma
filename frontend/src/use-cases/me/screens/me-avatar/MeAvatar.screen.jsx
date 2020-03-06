import React, { useState } from "react";
import {
    DigitButton,
    DigitDesign,
    DigitLayout,
    DigitSelectFile,
    DigitText,
    useDigitTranslations,
    useGammaUser
} from "@cthit/react-digit-components";
import translations from "./MeAvatar.screen.translations";
import { uploadUserAvatar } from "../../../../api/image/put.image.api";

const MeAvatar = () => {
    const [text] = useDigitTranslations(translations);
    const [file, setFile] = useState(null);
    const user = useGammaUser();

    return (
        <DigitLayout.Center>
            <DigitDesign.Card>
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle text={text.MeAvatarTitle} />
                </DigitDesign.CardHeader>
                <DigitDesign.CardBody>
                    {user.avatarUrl == null && (
                        <DigitText.Text bold text={text.NoAvatar} />
                    )}
                    {user.avatarUrl != null && (
                        <img src={user.avatarUrl} alt={"Avatar"} />
                    )}
                    <DigitSelectFile
                        image
                        onSelectFile={setFile}
                        selectedFileName={file != null ? file.name : null}
                    />
                    <DigitButton
                        primary
                        raised
                        disabled={file == null}
                        text={text.UploadImage}
                        onClick={() => {
                            uploadUserAvatar(file);
                        }}
                    />
                </DigitDesign.CardBody>
            </DigitDesign.Card>
        </DigitLayout.Center>
    );
};

export default MeAvatar;
