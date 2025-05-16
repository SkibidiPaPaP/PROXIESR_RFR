package com.example.proxiesr_rfr.ui.trees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proxiesr_rfr.databinding.FragmentTreesBinding
import com.google.android.material.snackbar.Snackbar
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.content.Context
import org.osmdroid.config.IConfigurationProvider
import java.io.File

class TreeSpeciesFragment : Fragment() {

    private var _binding: FragmentTreesBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: MapView

    // Rizal Park coordinates
    private val RIZAL_PARK_LAT = 14.5831
    private val RIZAL_PARK_LON = 120.9794
    private val DEFAULT_ZOOM = 18.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTreesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeOSMDroid()
        setupMap()

        binding.addMarkerButton.setOnClickListener {
            addMarkerFromInput()
        }
    }

    private fun initializeOSMDroid() {
        val ctx = requireContext()
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = ctx.packageName
        val osmConfig: IConfigurationProvider = Configuration.getInstance()
        val basePath = File(ctx.cacheDir.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        val tileCache = File(osmConfig.osmdroidBasePath.absolutePath, "tile")
        osmConfig.osmdroidTileCache = tileCache
    }

    private fun setupMap() {
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        
        val mapController = map.controller
        mapController.setZoom(DEFAULT_ZOOM)
        
        // Set initial position to Rizal Park
        val rizalPark = GeoPoint(RIZAL_PARK_LAT, RIZAL_PARK_LON)
        mapController.setCenter(rizalPark)
        
        // Add initial marker at Rizal Park
        addMarker(rizalPark, "Rizal Park")
    }

    private fun convDMStoDD(degrees: Double, minutes: Double, seconds: Double, direction: Char): Double {
        var dd = degrees + (minutes / 60.0) + (seconds / 3600.0)
        if (direction == 'S' || direction == 'W' || direction == 's' || direction == 'w') {
            dd = -dd
        }
        return dd
    }

    private fun parseDMSString(dmsStr: String): Double {
        try {
            // Remove all spaces and convert to uppercase for consistent parsing
            val cleanStr = dmsStr.replace(" ", "").uppercase()
            
            // Extract direction (N, S, E, W)
            val direction = cleanStr.last()
            if (!listOf('N', 'S', 'E', 'W').contains(direction)) {
                // If no direction found, treat as decimal degrees
                return dmsStr.toDouble()
            }

            // Remove the direction character for further parsing
            val numbers = cleanStr.substring(0, cleanStr.length - 1)
            
            // Split by degrees (°), minutes ('), and seconds (")
            val parts = numbers.split("°", "'", "\"")
            if (parts.size >= 3) {
                val degrees = parts[0].toDouble()
                val minutes = parts[1].toDouble()
                val seconds = parts[2].toDouble()
                return convDMStoDD(degrees, minutes, seconds, direction)
            } else {
                // If the format doesn't match DMS, try to parse as decimal degrees
                return dmsStr.toDouble()
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid coordinate format. Use either DMS (e.g., 40°26'46\"N) or decimal degrees")
        }
    }

    private fun addMarkerFromInput() {
        try {
            val latitudeStr = binding.latitudeInput.text.toString()
            val longitudeStr = binding.longitudeInput.text.toString()

            // Convert input strings to decimal degrees
            val latitude = parseDMSString(latitudeStr)
            val longitude = parseDMSString(longitudeStr)

            if (isValidLatLng(latitude, longitude)) {
                val position = GeoPoint(latitude, longitude)
                addMarker(position, "Custom Location")
                map.controller.animateTo(position)
                
                // Clear inputs after adding marker
                binding.latitudeInput.text?.clear()
                binding.longitudeInput.text?.clear()
            } else {
                showError("Invalid coordinates. Latitude must be between -90 and 90, and longitude between -180 and 180.")
            }
        } catch (e: IllegalArgumentException) {
            showError(e.message ?: "Invalid coordinate format")
        } catch (e: Exception) {
            showError("Please enter valid coordinates")
        }
    }

    private fun addMarker(position: GeoPoint, title: String) {
        val marker = Marker(map)
        marker.position = position
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        map.overlays.add(marker)
        map.invalidate()
    }

    private fun isValidLatLng(lat: Double, lng: Double): Boolean {
        return lat in -90.0..90.0 && lng in -180.0..180.0
    }

    private fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 