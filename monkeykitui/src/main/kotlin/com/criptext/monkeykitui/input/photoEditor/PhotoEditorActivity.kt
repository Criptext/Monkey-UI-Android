package com.criptext.monkeykitui.input.photoEditor

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.AsyncTask
import android.support.v4.app.FragmentManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.criptext.monkeykitui.R

import com.soundcloud.android.crop.Crop

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by gesuwall on 9/8/15.
 */
class PhotoEditorActivity : AppCompatActivity() {
    private var photo: ImageView? = null
    private var retainedFragment: RetainEditPhoto? = null
    private lateinit var tempFile : File
    lateinit var photoFilePath: String

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        tempFile = File(cacheDir.absolutePath + "/tempEditedPhoto.png")
        retainFragment()
        //set Title
        val title = intent.getStringExtra(titlePhotoEditor) ?: "Edit Photo"
        photoFilePath = intent.getStringExtra(destinationPath)
        supportActionBar!!.title = title

        photo = findViewById(R.id.photo) as ImageView?

        //set Photo
        setEditingPhoto()
        //System.out.println("degs:  " +  retainedFragment.getDegrees());
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**

     * @return El codigo del layout para esta instancia de EditPhoto
     */
    protected val layout: Int
        get() = R.layout.activity_photo_editor

    /**
     * Coloca la imagen que se esta editando en el ImageView principal del activity. No recibe
     * parametros porque con los datos de retainedFragment se sabe de donde sacar la imagen
     * correspondiente.
     */
    private fun setEditingPhoto() {
        val imgpath = bitmapUri
        //System.out.println("imgpath is: " + imgpath);
        photo!!.setImageURI(null)
        photo!!.setImageURI(imgpath)
        //Rotar imagen de acuerdo al estado anterior
        if (retainedFragment!!.isRotated) {
            val rotated = getRotateDrawable(photo!!.drawable, retainedFragment!!.degrees.toFloat())
            photo!!.setImageDrawable(rotated)
        }

        //Fix photo height for rotation
        try {
            val input = contentResolver.openInputStream(imgpath)
            val onlyBoundsOptions = BitmapFactory.Options()
            onlyBoundsOptions.inJustDecodeBounds = true
            onlyBoundsOptions.inDither = true//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
            input.close()
            if (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) {
                val scale = resources.displayMetrics.density
                val dpAsPixels = (45 * scale + 0.5f).toInt()
                photo!!.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Consigue la referencia al Fragment que se encarga de guarda los datos persistentes del Activity EditPhoto.
     * La convencion usada es siempre guardar el Fragment de Persistencia con el tag "data". Si el
     * Fragment ya existe lo saca del Fragment Manager para sacar los datos recuperados tras los
     * cambios de configuracion. De lo contrario, crea una nueva instancia del Fragment y lo inicializa
     * con los valores por defecto.
     */
    private fun retainFragment() {
        // find the retained fragment on activity restarts
        val fm = supportFragmentManager
        retainedFragment = fm.findFragmentByTag("data") as RetainEditPhoto?

        // create the fragment and data the first time
        if (retainedFragment == null) {
            // add the fragment
            retainedFragment = RetainEditPhoto()
            fm.beginTransaction().add(retainedFragment, "data").commit()
            retainedFragment!!.isEdited = false
            retainedFragment!!.degrees = 0
            tempFile.delete() // delete temp file since this is the first time
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_photo_editor, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> onBackPressed()
            R.id.action_crop -> {

                val origin = bitmapUri
                val destination = Uri.fromFile(tempFile)
                if (retainedFragment!!.isRotated)
                // Si hay grados tengo que crear el bitmap rotado
                    exportBitmap(origin, retainedFragment!!.degrees, Runnable {
                        retainedFragment!!.isEdited = true
                        retainedFragment!!.degrees = 0
                        Crop.of(destination, destination).start(this@PhotoEditorActivity)
                    })
                else {
                    Crop.of(origin, destination).start(this)
                }
            }
            R.id.action_rot -> {
                val bitmapUri = bitmapUri
                val fromDegs = retainedFragment!!.degrees
                val toDegs = fromDegs - 90
                rotateImageView(fromDegs, toDegs)
                retainedFragment!!.degrees = toDegs
            }
            else -> {
            }
        }//onRotateBtnClick(bitmapUri);

        return true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && resultCode == Activity.RESULT_OK && requestCode == Crop.REQUEST_CROP) {
            println("CropRes: " + Crop.getOutput(data))
            retainedFragment!!.isEdited = true //Nueva foto asi que no hay rotacion
            rotateImageView(0, 0, 0)
            setEditingPhoto()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    public override fun onResume() {
        super.onResume()
    }

    /**
     * Crea un nuevo Drawable con rotacion
     * @param d Drawable original
     * *
     * @param angle grados a rotar
     * *
     * @return Drawable rotado
     */
    private fun getRotateDrawable(d: Drawable, angle: Float): Drawable {
        val arD = arrayOf(d)
        return object : LayerDrawable(arD) {
            override fun draw(canvas: Canvas) {
                canvas.save()
                canvas.rotate(angle, (d.bounds.width() / 2).toFloat(), (d.bounds.height() / 2).toFloat())
                super.draw(canvas)
                canvas.restore()
            }
        }
    }

    /**
     * Rota la imagen y la guarda en el archivo temporal en un nuevo thread. Cuando termina de
     * editar el bitmap actualiza la view
     * @param bitmapUri Uri del bitmap a editar
     * *
     * @param degs grados a rotar la imagen
     * *
     * @param callback Runnable a ejecutar cuando termine de exportarse
     */
    private fun exportBitmap(bitmapUri: Uri, degs: Int, callback: Runnable) {
        val resolver = this.contentResolver
        val rotateThread = Thread(Runnable {
            var bmp : Bitmap? = null
            try {
                var bmp = getBitmapFromUri(bitmapUri, resolver)
                if(bmp != null) {
                    bmp = rotateBitmap(bmp, degs)
                    saveBitmapToFile(bmp, tempFile)
                }
                photo!!.post(callback)
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                bmp?.recycle()
            }
        })

        rotateThread.run()
    }

    /**
     * Inicia una animacion de rotacion sobre la foto a ser editada
     * @param fromDegrees angulo de inicio
     * *
     * @param toDegrees angulo de fin
     */
    private fun rotateImageView(fromDegrees: Int, toDegrees: Int, duration: Int = 400) {
        val rotate = RotateAnimation(fromDegrees.toFloat(), toDegrees.toFloat(), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = duration.toLong()
        rotate.isFillEnabled = true
        rotate.fillAfter = true
        rotate.interpolator = LinearInterpolator()
        photo!!.startAnimation(rotate)

    }

    /**
     * Consigue el Uri del bitmap de acuerdo a si ha sido editado o no. Si ha sido editado hay que
     * sacarlo del archivo temporal, si no de la galeria
     * @return
     */
    protected val bitmapUri: Uri
        get() {
            if (retainedFragment!!.isEdited)
                return Uri.fromFile(tempFile)
            else
                return intent.data
        }

    /**
     * Callback de click del boton de Use/Send. Exporta la foto editada al archivo temporal y termina
     * el activity
     * @param view Boton que recibio el Click
     */
    fun done(view: View) {
        val intent = Intent()
        wrapItUp(intent, false)
    }

    protected val editedDegrees: Int
        get() = retainedFragment!!.degrees

    /**
     * Termina este activity con un resultado OK y regresa al anterior
     */
    protected fun wrapItUp(resultIntent: Intent, isEfimero: Boolean) {
        val resolver = this.contentResolver
        val bitmapUri = bitmapUri
        val degs = editedDegrees
        //final AQuery aq = new AQuery(this);
        //final File cache = aq.getCachedFile(MainActivity.URL + AccountManager.instance(getApplicationContext()).get_id() + ".png");
        object : AsyncTask<Void, Void, ByteArrayOutputStream>() {
            override fun doInBackground(vararg v: Void): ByteArrayOutputStream {
                var bmp: Bitmap? = null
                var fOut = ByteArrayOutputStream()
                try {

                    //edit new avatar bitmap
                    bmp = getBitmapFromUri(bitmapUri, resolver)
                    bmp = rotateBitmap(bmp!!, degs)

                    fOut = ByteArrayOutputStream()

                    val destFile = File(photoFilePath)
                    Log.d("PhotoEditor","dest path: $photoFilePath")
                    destFile.delete()

                    bmp!!.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                    destFile.writeBytes(fOut.toByteArray())
                    fOut.flush()

                    fOut.close()




                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    bmp?.recycle()
                }

                return fOut
            }

            override fun onPostExecute(result: ByteArrayOutputStream) {


                resultIntent.data = bitmapUri
                setResult(Activity.RESULT_OK, resultIntent)
                finish()

            }

        }.execute()

    }

    class RetainEditPhoto : Fragment() {

        // true si he editado la foto y debo de sacarla de la cache, de lo contrario tengo
        //que sacarla de la galeria
        var isEdited: Boolean = false
        var degrees: Int = 0

        // this method is only called once for this fragment
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            isEdited = false
            // retain this fragment
            retainInstance = true
        }

        val isRotated: Boolean
            get() = degrees % 360 != 0
    }

    companion object {
        val titlePhotoEditor = "PhotoEditorActivity.Title"
        val btnTxtPhotoEditor = "PhotoEditorActivity.ButtonText"
        val convoPhotoEditor = "PhotoEditorActivity.ConversationId"
        val destinationPath = "PhotoEditorActivity.destinationPath"

        /**
         * Recibe un uri y devuelve el bitmap encontrado
         * @param uri
         * *
         * @param resolver
         * *
         * @return
         * *
         * @throws FileNotFoundException
         * *
         * @throws IOException
         */
        @Throws(FileNotFoundException::class, IOException::class)
        fun getBitmapFromUri(uri: Uri, resolver: ContentResolver): Bitmap? {
            val THUMBNAIL_SIZE = 640.0
            var input: InputStream = resolver.openInputStream(uri)

            val onlyBoundsOptions = BitmapFactory.Options()
            onlyBoundsOptions.inJustDecodeBounds = true
            onlyBoundsOptions.inDither = true//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
            input.close()
            if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1)
                return null

            val originalSize = if (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) onlyBoundsOptions.outHeight else onlyBoundsOptions.outWidth

            val ratio = if (originalSize > THUMBNAIL_SIZE) originalSize / THUMBNAIL_SIZE else 1.0

            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)
            bitmapOptions.inDither = true//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//optional
            input = resolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
            input.close()
            return bitmap
        }

        private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
            val k = Integer.highestOneBit(Math.floor(ratio).toInt())
            if (k == 0)
                return 1
            else
                return k
        }

        /**
         * Rota un bitmap
         * @param source bitmap a rotar
         * *
         * @param degs grados a rotar
         * *
         * @return el bitma rotado grados
         */
        protected fun rotateBitmap(source: Bitmap, degs: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degs.toFloat())

            //Bitmap scaledBitmap = Bitmap.createScaledBitmap(source,source.getWidth(),source.getHeight(),true);
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }

        /**
         * Guarda un bitmap en el archivo temporal
         * @param source bitmap a guardar
         * *
         * @param dest referencia al File donde se guarda
         */
        private fun saveBitmapToFile(source: Bitmap, dest: File) {
            try {
                val fout = FileOutputStream(dest)
                source.compress(Bitmap.CompressFormat.PNG, 85, fout)
                fout.flush()
                fout.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
    }
}
