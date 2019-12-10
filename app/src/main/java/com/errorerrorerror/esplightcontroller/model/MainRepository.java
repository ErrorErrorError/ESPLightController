package com.errorerrorerror.esplightcontroller.model;

import com.errorerrorerror.esplightcontroller.model.device_ambilight.AmbilightRepository;
import com.errorerrorerror.esplightcontroller.model.device_music.MusicRepository;
import com.errorerrorerror.esplightcontroller.model.device_solid.SolidRepository;
import com.errorerrorerror.esplightcontroller.model.device_waves.WavesRepository;

import javax.inject.Inject;

public class MainRepository {
    private MusicRepository musicRepository;
    private SolidRepository solidRepository;
    private WavesRepository wavesRepository;
    private AmbilightRepository ambilightRepository;

    @Inject
    public MainRepository(MusicRepository musicRepository, SolidRepository solidRepository, WavesRepository wavesRepository, AmbilightRepository ambilightRepository) {
        this.musicRepository = musicRepository;
        this.solidRepository = solidRepository;
        this.wavesRepository = wavesRepository;
        this.ambilightRepository = ambilightRepository;
    }

    public MusicRepository getMusicRepository() {
        return musicRepository;
    }

    public SolidRepository getSolidRepository() {
        return solidRepository;
    }

    public WavesRepository getWavesRepository() {
        return wavesRepository;
    }

    public AmbilightRepository getAmbilightRepository() { return ambilightRepository; }
}
